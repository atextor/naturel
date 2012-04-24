package naturel.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import naturel.analysis.DepthFirstAdapter;
import naturel.generator.definitions.Sichtbarkeit;
import naturel.generator.definitions.VariableDef;
import naturel.helpers.Helper;
import naturel.node.AClass;
import naturel.node.ADeclaration;
import naturel.node.AInterfaceInterface;
import naturel.node.AMethod;
import naturel.node.ANamedVariable;
import naturel.node.ANaturel;
import naturel.node.APrivModifier;
import naturel.node.APrivStatModifier;
import naturel.node.AProtModifier;
import naturel.node.AProtStatModifier;
import naturel.node.APublModifier;
import naturel.node.APublStatModifier;
import naturel.node.PInterface;
import naturel.node.TIdentifier;

public class SammleKlassenMethoden extends DepthFirstAdapter {
	public class MethodenDef {
		public boolean statisch = false;
		public Sichtbarkeit sichtbarkeit = Sichtbarkeit.PUBLIC;
		public String name = "";
		public String rettype = null;
		public String cCodeName = "";
		public String position = "[]";
		public String className = null;
		public Map<String, VariableDef> params = new HashMap<String, VariableDef>();
		 // Leerer Konstruktor ist auch erlaubt
		public MethodenDef() {}
		public MethodenDef(boolean statisch, Sichtbarkeit sichtbarkeit, 
				String name, String rettype, String cCodeName, String position, String className) {
			this.statisch = statisch;
			this.sichtbarkeit = sichtbarkeit;
			this.name = name;
			this.rettype = rettype;
			this.cCodeName = cCodeName;
			this.position = position;
			this.className = className;
		}
	}
	
	public class ClassDef {
		public Sichtbarkeit sichtbarkeit = Sichtbarkeit.PUBLIC;
		public String name = "";
		public String superof = null;
		public List<String> interfaces = null;
		public Map<String, MethodenDef> methods = new HashMap<String, MethodenDef>();
		public Map<String, VariableDef> vars = new HashMap<String, VariableDef>();
		public String position = "[]";
		public boolean isInterface = false;
		
		public ClassDef() {};
		public ClassDef(Sichtbarkeit sichtbarkeit, String name, String superof, String position) {
			this.sichtbarkeit = sichtbarkeit;
			this.name = name;
			this.superof = superof;
			this.position = position;
		}
	}

	/**
	 * Eine Map aller Klassen:
	 */
	private Map<String, ClassDef> allClasses = new HashMap<String, ClassDef>(); 
	
	/**
	 * Map von Methodennamen auf Methodendeklarationen der aktuellen durchlaufenen Klasse
	 */
	private ClassDef aktklass = null;
	
	private MethodenDef curMethod = null;
	
	private String curClassName = "";
	
	private String mainClassName = null;
	
	// Zum Mitzählen der Parameter
	private int paramPos = -1;
	
	/**
	 * Es sollen nur die Klassenvariablen gemerkt werden
	 */
	private boolean inMethod = false; 

	public String getMainClassName() {
		return mainClassName;
	}
	
	/**
	 * Dem Helper die Klassen auch verfügbar machen
	 */
	public SammleKlassenMethoden() {
		Helper.setAlleKlassenMethoden(allClasses);
	}

	/**
	 * Gibt das Objekt zurück, das alle Klassen und Methoden enthält
	 */
	public Map<String, ClassDef> getAlleKlassenMethoden() {
		return allClasses;
	}
	
	/**
	 * Global definierte Methoden
	 */
	@Override
	public void inANaturel(ANaturel node) {
		// add well-known Klasses and methods
		
		ClassDef core = new ClassDef(Sichtbarkeit.PUBLIC, "", null, "[build-in]"); 
		allClasses.put("", core);
		
		//Map<String, MethodenDef> defa = new HashMap<String, MethodenDef>();
		//methoden.put("", defa);
		core.methods.put("return",  new MethodenDef(true, Sichtbarkeit.PUBLIC, "return",  null, "return", "[build-in]", ""));
		core.methods.put("out",     new MethodenDef(true, Sichtbarkeit.PUBLIC, "out",     null, "out", "[build-in]", ""));
		core.methods.put("in",      new MethodenDef(true, Sichtbarkeit.PUBLIC, "in",      "str", "in", "[build-in]", ""));
		core.methods.put("str_new", new MethodenDef(true, Sichtbarkeit.PUBLIC, "str_new", "str", "str_new", "[build-in]", ""));
		core.methods.put("num_new", new MethodenDef(true, Sichtbarkeit.PUBLIC, "num_new", "num", "num_new", "[build-in]", ""));
		
		ClassDef object = new ClassDef(Sichtbarkeit.PUBLIC, "Object", null, "[build-in]");
		//Map<String, MethodenDef> object = new HashMap<String, MethodenDef>();
		allClasses.put("Object", object);
		object.methods.put("tostr", new MethodenDef(false, Sichtbarkeit.PUBLIC, "tostr", "str", "tostr", "[build-in]", "Object"));
		
		ClassDef num = new ClassDef(Sichtbarkeit.PUBLIC, "num", "Object", "[build-in]");
		//Map<String, MethodenDef> num = new HashMap<String, MethodenDef>();
		allClasses.put("num", num);
		num.methods.put("addNum", new MethodenDef(false, Sichtbarkeit.PUBLIC, "addNum", "num", "addNum", "[build-in]", "num"));
		num.methods.put("subNum", new MethodenDef(false, Sichtbarkeit.PUBLIC, "subNum", "num", "subNum", "[build-in]", "num"));
		num.methods.put("multNum",new MethodenDef(false, Sichtbarkeit.PUBLIC, "multNum", "num", "multNum", "[build-in]", "num"));
		num.methods.put("divNum", new MethodenDef(false, Sichtbarkeit.PUBLIC, "divNum", "num", "divNum", "[build-in]", "num"));
		num.methods.put("modNum", new MethodenDef(false, Sichtbarkeit.PUBLIC, "modNum", "num", "modNum", "[build-in]", "num"));
		
		num.methods.put("eq", new MethodenDef(false, Sichtbarkeit.PUBLIC, "eq", "bool", "eq", "[build-in]", "num"));
		num.methods.put("neq", new MethodenDef(false, Sichtbarkeit.PUBLIC, "neq", "bool", "neq", "[build-in]", "num"));
		num.methods.put("lteq", new MethodenDef(false, Sichtbarkeit.PUBLIC, "lteq", "bool", "lteq", "[build-in]", "num"));
		num.methods.put("lt", new MethodenDef(false, Sichtbarkeit.PUBLIC, "lt", "bool", "lt", "[build-in]", "num"));
		num.methods.put("gteq", new MethodenDef(false, Sichtbarkeit.PUBLIC, "gteq", "bool", "gteq", "[build-in]", "num"));
		num.methods.put("gt", new MethodenDef(false, Sichtbarkeit.PUBLIC, "gt", "bool", "gt", "[build-in]", "num"));
		
		ClassDef bool = new ClassDef(Sichtbarkeit.PUBLIC, "bool", "Object", "[build-in]");
		allClasses.put("bool", bool);

		//Map<String, MethodenDef> str = new HashMap<String, MethodenDef>();
		ClassDef str = new ClassDef(Sichtbarkeit.PUBLIC, "str", "Object", "[build-in]");
		allClasses.put("str", str);
		str.methods.put("append", new MethodenDef(false, Sichtbarkeit.PUBLIC, "append", "str", "append", "[build-in]", "str"));
		str.methods.put("asnum", new MethodenDef(false, Sichtbarkeit.PUBLIC, "asnum", "num", "asnum", "[build-in]", "str"));
		
		ClassDef list = new ClassDef(Sichtbarkeit.PUBLIC, "list", "Object", "[build-in]");
		allClasses.put("list", list);
	}
	
	/**
	 * Anfang einer Klasse: Setzt den aktuellen Klassennamen
	 */
	@Override
	public void inAClass(AClass node) {
		//curClassName = Helper.listToString(node.getName());
		aktklass = new ClassDef();
		if (node.getModifier() instanceof APublModifier) {
			aktklass.sichtbarkeit = Sichtbarkeit.PUBLIC;
		} else if (node.getModifier() instanceof APrivModifier) {
			aktklass.sichtbarkeit = Sichtbarkeit.PRIVATE;
		} else if (node.getModifier() instanceof AProtModifier) {
			aktklass.sichtbarkeit = Sichtbarkeit.PROTECTED;
		} else if (node.getModifier() instanceof APublStatModifier) {
			aktklass.sichtbarkeit = Sichtbarkeit.PUBLIC;
			aktklass.isInterface = true;
		} else if (node.getModifier() instanceof APrivStatModifier) {
			aktklass.sichtbarkeit = Sichtbarkeit.PRIVATE;
			aktklass.isInterface = true;
		} else if (node.getModifier() instanceof AProtStatModifier) {
			aktklass.sichtbarkeit = Sichtbarkeit.PROTECTED;
			aktklass.isInterface = true;
		}
		
		for (TIdentifier id : node.getName()) {
			if (aktklass.position.equals("[]")) {
				aktklass.position = "[" +  id.getLine() + "," + id.getPos() + "]";
			}
			if (!aktklass.name.equals("")) {
				aktklass.name = aktklass.name + ".";
			}
			aktklass.name = aktklass.name + id.getText().toString().trim();
		}
		if (aktklass.name == null) {
			throw new RuntimeException("Internal Error: name?!?");
		}
		curClassName = aktklass.name;
		for (TIdentifier id : node.getSuper()) {
			if (aktklass.superof == null) {
				aktklass.superof = id.getText().toString().trim();
			} else {
				aktklass.superof = aktklass.superof + "." + aktklass.superof ;
			}
		}
		if (aktklass.superof == null) {
			// ALLES kommt von Object!
			aktklass.superof = "Object";
		}
		for (PInterface interf : node.getInterfaces()) {
			String interfname = "";
			if ( ! (interf instanceof AInterfaceInterface)) {
				throw new RuntimeException("Internal Error: interf instanceof: " + interfname.getClass().getSimpleName());
			}
			AInterfaceInterface ainterf = (AInterfaceInterface) interf;
			
			for (TIdentifier id : ainterf.getName()) {
				if (interfname.equals("")) {
					interfname = id.toString().trim();
				} else {
					interfname = interfname + "." + id.toString().trim();
				}
			}
			if (interfname.equals("")) {
				throw new RuntimeException("Class " + aktklass.name + ": interfacename not set");
			}
			if (aktklass.interfaces == null) {
				aktklass.interfaces = new ArrayList<String>();
			}
			aktklass.interfaces.add(interfname);
		}
		ClassDef cd = allClasses.get(aktklass.name); 
		if (cd != null) {
			throw new RuntimeException("Class " + aktklass.name + " at " + aktklass.position + " redefined from " + cd.position);
		}
				
		allClasses.put(aktklass.name, aktklass);
		
		//aktklass.name = node.getName().toString().trim();
		
		//superOf.put(curClassName, Helper.listToString(node.getSuper()));
	}
	
	/**
	 * Ende einer Klasse: Infos zurücknehmen
	 */
	@Override
	public void outAClass(AClass node) {
		curClassName = "";
		aktklass = null;
	}
	
	/**
	 * Anfang einer Methode. Merken
	 */
	@Override
	public void inAMethod(AMethod node) {
		String name = node.getName().toString().trim();
		curMethod = new MethodenDef();
		aktklass.methods.put(name, curMethod);
		curMethod.position = "[" + node.getName().getLine() + "," + node.getName().getPos() + "]";
		curMethod.className = curClassName;
		if (node.getModifier() instanceof APublStatModifier
				|| node.getModifier() instanceof APrivStatModifier
				|| node.getModifier() instanceof AProtStatModifier) {
			curMethod.statisch = true;
		} else {
			curMethod.statisch = false;
		}
		if (node.getModifier() instanceof APublStatModifier
				|| node.getModifier() instanceof APublModifier) {
			curMethod.sichtbarkeit = Sichtbarkeit.PUBLIC;
		}
		if (node.getModifier() instanceof APrivStatModifier
				|| node.getModifier() instanceof APrivModifier) {
			curMethod.sichtbarkeit = Sichtbarkeit.PRIVATE;
		}
		if (node.getModifier() instanceof AProtStatModifier
				|| node.getModifier() instanceof AProtModifier) {
			curMethod.sichtbarkeit = Sichtbarkeit.PROTECTED;
		}
		curMethod.name = name;
		//def.cCodeName = "function_" + curClassName + "_" + name;
		curMethod.cCodeName = curClassName + "_" + name;
		if (name.equals("new")) {
			//Konstruktoren geben die Klasse zurück
			curMethod.rettype = curClassName;
		} else {
			curMethod.rettype = Helper.typeToString(node.getType());
		}
		
		if (name.equals("main") && curMethod.statisch) {
			mainClassName = curClassName;
		}
		inMethod = true;
		paramPos = 0;
	}

	@Override
	public void outAMethod(AMethod node) {
		inMethod = false;
		curMethod = null;
		paramPos = -1;
	}

	public void inANamedVariable(ANamedVariable node) {
		// Nur die Klassenvariablen merken
		boolean param = false;
		int paramposition = -1;
		if ((inMethod) && (node.parent() instanceof AMethod)) {
			// Das ist ein Parameter
			param = true;
			paramposition = paramPos;
			paramPos = paramPos + 1;
		}
		if (param || !inMethod) {
			// Zum rausfinden der Sichtbarkeit / statik das declaration dazu holen.
			ADeclaration dec = null;
			if (node.parent() instanceof ADeclaration) {
				dec = (ADeclaration) node.parent();
			}
			
			VariableDef varDef = new VariableDef();
			varDef.name = node.getName().toString().trim();
			varDef.pos = "[" + node.getName().getLine() + "," + node.getName().getPos() + "]";
			varDef.hierarchy = 0;
			if (param) {
				varDef.cCodeName = "varParam" + "_" + Helper.getNextVarNum();
			} else {
				varDef.cCodeName = "varClass" + "_" + Helper.getNextVarNum();
				varDef.className = curClassName;
			}
			varDef.parameter = param;
			varDef.paramposition = paramposition;

			if (dec != null) {
				varDef.statisch = (dec.getModifier() instanceof APublStatModifier) ||
									(dec.getModifier() instanceof APrivStatModifier) ||
									(dec.getModifier() instanceof AProtStatModifier);
			}
			
			

			varDef.typ  = Helper.typeToString(node.getType());
			//System.out.println("XX: " + varDef.name + " [" + node.getName().getLine()+","+node.getName().getPos()+"] " + varDef.hierarchy);
			// Gibt es schon so eine Variable?
			if (param) {
				for (VariableDef vd : curMethod.params.values()) {
					if (vd.name.equals(varDef.name)) {
						throw new RuntimeException("Parameter redefined [" 
								+ node.getName().getLine() + ", " + node.getName().getPos() + "]: " + varDef.name 
								+ " previous: " + vd.pos + ": " + vd.name);
					}
				}
				curMethod.params.put(varDef.name, varDef);
			} else {
	 			for (VariableDef vd : aktklass.vars.values()) {
					if (vd.name.equals(varDef.name)) {
						throw new RuntimeException("Class-Variable redefined [" 
								+ node.getName().getLine() + ", " + node.getName().getPos() + "]: " + varDef.name 
								+ " previous: " + vd.pos + ": " + vd.name);
					}
				}
	 			aktklass.vars.put(varDef.name, varDef);
			}
			
		}
	}

	public void ausgeben() {
		for (ClassDef curclass : allClasses.values()) {
			System.out.println("Class: " + curclass.name+" at " + curclass.position + ", Super: " + curclass.superof + ":");
			System.out.println("\tInterfaces");
			for (String interf : curclass.interfaces) {
				System.out.println("\t\t" + interf);
			}
			System.out.println("\tVariables:");
			for (VariableDef var : curclass.vars.values()) {
				System.out.println("\t\t" + var.name + " at " + var.pos + ", type: " + var.typ);
			}
			System.out.println("\tMethods:");
			for (MethodenDef md : curclass.methods.values()) {
				System.out.println("\t\t" + md.name + ":");
				System.out.println("\t\t\tStatisch: " + md.statisch);
				System.out.println("\t\t\tSichtbarkeit: " + md.sichtbarkeit);
				System.out.println("\t\t\tName: " + md.name);
				System.out.println("\t\\ttTyp: " + md.rettype);
				System.out.print("\t\t\tParams: ");
				System.out.println("");
				System.out.println("\t\t\tcCodeName: " + md.cCodeName);
			}
		}
	}

}
