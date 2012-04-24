package naturel.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import naturel.analysis.DepthFirstAdapter;
import naturel.generator.SammleKlassenMethoden.ClassDef;
import naturel.generator.SammleKlassenMethoden.MethodenDef;
import naturel.generator.definitions.VariableDef;
import naturel.helpers.Helper;
import naturel.node.ABlock;
import naturel.node.AClass;
import naturel.node.ADotExp;
import naturel.node.AFnumExp;
import naturel.node.AListExp;
import naturel.node.AMethod;
import naturel.node.AMethodcallExp;
import naturel.node.ANamedVariable;
import naturel.node.ANumExp;
import naturel.node.AStringExp;
import naturel.node.PExp;

public class DepthVarManager extends DepthFirstAdapter {
	protected Map<String, ClassDef> alleKlassenMethoden = null;
	
	/**
	 * aktuelles Methoden Objekt
	 */
	protected MethodenDef curMethod = null;
	
	/**
	 * Aktuelles KlassenObjekt
	 */
	protected ClassDef curClassDef = null;
	
	/**
	 * Aktueller Klassenname - wird zum Prefix für Funktionsnamen
	 */
	protected String curClassName = null;
	
	/**
	 * Sonst erhöht jeder erbe die Nummern, ohne das zu nutzen.
	 * Ist zwar nicht schlimm, aber unschön.
	 */
	protected Boolean generateCCodeNames = false;
	
	/**
	 * Alle Variablen merken: Liste
	 */
	protected List<VariableDef> allVariables = new ArrayList<VariableDef>();
	
	/**
	 * Übergeben des Objektes, das alle Klassen und Methoden enthält
	 * @param alleKlassenMethoden
	 */
	public void setAlleKlassenMethoden(Map<String, ClassDef> alleKlassenMethoden) {
		this.alleKlassenMethoden = alleKlassenMethoden;
	}
	
	@Override
	public void inANamedVariable(ANamedVariable node) {
		// Variable anlegen
		// Variable merken
		if (curMethod == null) {
			// Klassenvariablen sind in "alleKlassenMEthoden" gespeichert
		} else {
			VariableDef varDef = new VariableDef();
			varDef.name = node.getName().toString().trim();
			varDef.pos = "[" + node.getName().getLine() + "," + node.getName().getPos() + "]";
			varDef.hierarchy = 0;
			if (generateCCodeNames) {
				varDef.cCodeName = "varLocal" + "_" + Helper.getNextVarNum();
			} else {
				varDef.cCodeName = "NotGenerated";
			}
		
			varDef.typ  = Helper.typeToString(node.getType());
			//System.out.println("XX: " + varDef.name + " [" + node.getName().getLine()+","+node.getName().getPos()+"] " + varDef.hierarchy);
			// Gibt es schon so eine Variable?
			for (VariableDef vd : allVariables) {
				if (vd.name.equals(varDef.name)
						&& (vd.hierarchy == varDef.hierarchy) ) {
					throw new RuntimeException("Variable redefined [" 
							+ node.getName().getLine() + "," + node.getName().getPos() + "]: " + varDef.name 
							+ " previous: " + vd.pos + ": " + vd.name);
				}
			}
			allVariables.add(varDef);
		}
	}

	@Override
	public void inABlock(ABlock node) {
		// Variablen-"Hierarchie". Siehe Kommentar in der Klasse VariableDef 
		for (VariableDef vd : allVariables) {
			vd.hierarchy += 1;
		}
	}

	@Override
	public void outABlock(ABlock node) {
		// Variablen-"Hierarchie". Siehe Kommentar in der Klasse VariableDef
		for (Iterator<VariableDef> i = allVariables.iterator(); i.hasNext();) {
			VariableDef vd = i.next();
			vd.hierarchy -= 1;
			
			if (vd.hierarchy < 0) {
				// Die gibt es nicht mehr. Löschen
				i.remove();
			}
		}
	}
	
    /**
     * Sucht eine Variable im Array
     * @param name
     * @return das Objekt, das die Variable repraesentiert
     */
    protected VariableDef searchVariable(String name, String className) {
    	VariableDef ret = null;
		for (VariableDef vd : allVariables) {
			if (((vd.name.equals(name)) && (className == null)) ||
					((vd.name.equals(name) && (className != null) && (vd.className.equals(className))))) {
				// Gleiche Variablenname. Aber der mit der niedrigsten Hierarchie gewinnt,
				//  da dieser Block-Lokal ist
				if ((ret == null) ||
						(ret.hierarchy > vd.hierarchy)) {
					ret = vd;
				}
			}
		}
		if ((ret == null) && (curMethod != null)) {
			// Keine Lokale Variable? Evtl. ein Parameter
			ret = curMethod.params.get(name);
		}
		if (ret == null) {
			// Kein Parameter? Evtl. eine Klassenvariable
			//System.out.println("XX: " + name);
			if (className == null) {
				ret = curClassDef.vars.get(name);
				
				if (ret == null) {
					// Evtl. was geerbtes?
					ClassDef cdef = curClassDef;
					while ((cdef.superof != null) && (ret == null)) {
						cdef = alleKlassenMethoden.get(cdef.superof);
						// TODO: Null-Abfragen? Typecheck2 ist aber schon gelaufen, sollte also nichts ausamchen
						ret = cdef.vars.get(name);
					}
				}
			} else {
				ClassDef cdef = alleKlassenMethoden.get(className);
				if (cdef == null) {
					throw new RuntimeException("Class: " + className + " not found");
				}
				ret = cdef.vars.get(name);
				
				if (ret == null) {
					// Evtl. was geerbtes?
					while ((cdef != null) && (ret == null)) {
						cdef = alleKlassenMethoden.get(cdef.superof);
						// TODO: Null-Abfragen? Typecheck2 ist aber schon gelaufen, sollte also nichts ausamchen
						ret = cdef.vars.get(name);
					}
				}
			}
		}
		return ret;
    }
    
    /**
     * Sucht die Methodendefinition 
     */
    protected MethodenDef searchMethode(String name, String className) {
    	if (className == null) {
    		MethodenDef mdef = searchMethode(name, curClassName);
    		if (mdef == null) {
    			// Vielleicht in der Core-Klasse 
    			mdef = searchMethode(name, "");
    		}
    		return mdef;
    	} else {
        	ClassDef cdef = alleKlassenMethoden.get(className);
        	if (cdef == null) {
        		// Klasse nicht gefunden
        		return null;
        	}
        	MethodenDef mdef = cdef.methods.get(name);
        	if (mdef == null) {
        		// Evtl. in der Super-Klasse?
        		if (cdef.superof != null) {
        			mdef = searchMethode(name, cdef.superof); 
        		}
        	} 
        	return mdef;
    	}
    }
	
	@Override
	public void inAClass(AClass node) {
		curClassName = Helper.listToString(node.getName());
		if (alleKlassenMethoden == null) {
			throw new RuntimeException("Collected Classes not set");
		}
		curClassDef = alleKlassenMethoden.get(curClassName);
		if (curClassDef == null) {
			throw new RuntimeException("Collecting of methods and classes failed (" + curClassName + ")");
		}
	}
	
	/**
	 * Ende einer Klasse: Dinge resetten
	 */
	@Override
	public void outAClass(AClass node) {
		allVariables.clear();
		curClassDef = null;
		curClassName = null;
	}

	
	/**
	 * Start einer Methode
	 */
	@Override
	public void inAMethod(AMethod node) {
		String name =  node.getName().toString().trim();
		curMethod = curClassDef.methods.get(name);
		if (curMethod == null) {
			throw new RuntimeException("Collecting of methods and classes failed. Class/Method: " + curClassName + "/" + name);
		}
		
		// "This" variable anlegen
		VariableDef thisvar = new VariableDef();
		
		thisvar.name = "this";
		thisvar.typ  = curClassDef.name;
		thisvar.cCodeName = "this";
		thisvar.pos = "[build-in]";
		
		allVariables.add(thisvar);
		
		// Variablen-"Hierarchie". Siehe Kommentar in der Klasse VariableDef 
		for (VariableDef vd : allVariables) {
			vd.hierarchy += 1;
		}
	}
	
	/**
	 * Aus einer Methode
	 */
	@Override
	public void outAMethod(AMethod node) {
		curMethod = null;
		
		allVariables.clear();
	}
	
	protected String getType(PExp node) {
		return getType(node, null);
	}
	
	protected String getType(PExp node, String inclass) {
		if (node instanceof ANumExp) {
			return "num";
		} 
		if (node instanceof AFnumExp) {
			return "fnum";
		}
		if (node instanceof AStringExp) {
			return "str";
		}
		
//		if (node instanceof IBoolNode) {
//			return "bool";
//		}
		if (node instanceof AMethodcallExp) {
			AMethodcallExp methcall = (AMethodcallExp)node;
			if ((methcall.getArgs() == null) || (methcall.getArgs().size()<=0)) {
				// Das ist eine Variable
				String name = methcall.getId().toString().trim();
				VariableDef aktVarDef = searchVariable(name, inclass);
				if (aktVarDef == null) {
					// evtl. ist das eine Klasse?
					if (alleKlassenMethoden.get(name) != null) {
						return name;
					}
				}
				if (aktVarDef == null) {
					throw new RuntimeException("Undefined variable [" + methcall.getId().getLine()
							+ ", " + methcall.getId().getPos() + "]: " +name);
				}
				return aktVarDef.typ;
			} else {
				// Methode
				MethodenDef zielmethode = null;
				String methodName = "?";
				if (methcall.getId() != null) {
					methodName = methcall.getId().toString().trim();
					// Versuchen aktuelle Klasse, und default Klasse
					zielmethode = searchMethode(methodName, inclass); 
				}
				if (zielmethode == null) {
					// Nö. Hilfe!
					throw new RuntimeException("Invalid method call[" + methcall.getId().getLine()
							+ "," + methcall.getId().getPos() + "]: Method not found: "
							+ methcall.getId());
				}
				return zielmethode.rettype;
			}
		} else if (node instanceof ADotExp) {
			ADotExp dot = (ADotExp) node;
			// Das rechteste vom Dot ist der Typ
			return getType(dot.getR(),getType(dot.getL(), inclass));
		} else if (node instanceof AListExp) {
			// Das wird interessant.
			// Es kann sein:
			// [package.]Klasse.statischeVariable
			// Objetk.variable
			// Objekt.mehtode()
			// Objekt.methode()...

			// Wenn das eine Variable ist..
			//VariableDef merkVar = null;
			String merkKlasse = "";
			String merkType = null;
			for (PExp ex : ((AListExp)node).getList()) {
				if (merkType != null) {
					// Vorher war da schon was?
					if (ex instanceof AMethodcallExp) {
						AMethodcallExp meth = (AMethodcallExp) ex;
						if ((meth.getArgs() == null) || (meth.getArgs().size() <= 0)) {
							// TODO: Variable
							VariableDef vdef = searchVariable(meth.getId().toString().trim(), merkType);
							if (vdef == null) {
								throw new RuntimeException("Variable \"" + meth.getId().toString().trim() 
										+ "\" on \"" + merkType + "\" not found at ["
										+ meth.getId().getLine() + "," + meth.getId().getPos() + "]");
							}
							merkType = vdef.typ;
						} else {
							MethodenDef mdef = searchMethode(meth.getId().toString().trim(), merkType);
							if (mdef == null) {
								throw new RuntimeException("Method \"" + meth.getId().toString().trim() 
										+ "\" on \"" + merkType + "\" not found at ["
										+ meth.getId().getLine() + "," + meth.getId().getPos() + "]");
							}
							merkType = mdef.rettype;
						}
					} else {
						throw new RuntimeException("Internal Exception: node not instanceof AMethodcallExp");
					}
				} else if ( ! merkKlasse.equals("")) {
					// Vorher wurde schon eine Klasse erkannt?
					// Es kann jetzt kommen:
					// Klasse (das vorher war ein Paketname)
					// statische Variable / Methode der Klasse
					if (ex instanceof AMethodcallExp) {
						AMethodcallExp meth = (AMethodcallExp) ex;
						if ((meth.getArgs() == null) || (meth.getArgs().size() <= 0)) {
							boolean gefunden = false;
							for (String s : alleKlassenMethoden.keySet()) {
								if (s.startsWith(merkKlasse + "." + meth.getId().toString().trim())) {
									gefunden = true;
								}
							}
							if (gefunden) {
								merkKlasse = merkKlasse + "." + meth.getId().toString().trim();
							} else {
            					// Eine statische Variable?
								VariableDef vdef = searchVariable(meth.getId().toString().trim(), merkKlasse);
								if (vdef == null) {
									throw new RuntimeException("Class-Variable not found: " + merkKlasse + "." + merkKlasse);
								}
								merkKlasse = "";
								merkType = vdef.typ;
            				}
						} else {
							// TODO - methode (statische Methode auf einer Klase)
						}
					} else {
						throw new RuntimeException("Internal Exception: node not instanceof AMethodcallExp");
					}
				} else if (ex instanceof AMethodcallExp) {
					// Vorher wurde nichts erkannt? Erster Durchlauf?
					AMethodcallExp meth = (AMethodcallExp) ex;
					if ((meth.getArgs() == null) || (meth.getArgs().size() <= 0)) {
						// Das ist eine Variable / Klasse / Paket
						// Variable?
						VariableDef vardef = searchVariable(meth.getId().toString().trim(), null);
						if (vardef != null) {
							//merkVar = vardef;
							// Wenn das das letzte ist, so ist das der Typ, den wir wollen
							merkType = vardef.typ;
						} else {
							// Klasse?
							boolean gefunden = false;
							for (String s : alleKlassenMethoden.keySet()) {
								if (s.startsWith(meth.getId().toString().trim())) {
									gefunden = true;
								}
							}
							if (gefunden) {
								merkKlasse = meth.getId().toString().trim();
							} else {
            					throw new RuntimeException("Could't identify Identifier ["
            							+ meth.getId().getLine() + ", "
            							+ meth.getId().getPos() + "] : " + meth.getId().toString().trim());
            				}
						}
					} else {
						// Methodenaufruf
						MethodenDef methdef = searchMethode(meth.getId().toString().trim(), null);
						if (methdef == null) {
							throw new RuntimeException("Method " + meth.getId() + "not found.");
						}
						merkType = methdef.rettype;
					}
				}
			}
			//TODO: Rumheulen, wenn nichts gefunden wurde
			return merkType;
		}
		System.out.println("Error: getType: " + node.getClass().getSimpleName());
		
		return "?";
	}

}
