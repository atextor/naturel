package naturel.generator;

import java.util.ArrayList;
import java.util.List;

import naturel.Main;
import naturel.generator.SammleKlassenMethoden.ClassDef;
import naturel.generator.SammleKlassenMethoden.MethodenDef;
import naturel.generator.definitions.Sichtbarkeit;
import naturel.generator.definitions.VariableDef;
import naturel.helpers.Helper;
import naturel.node.AAndExp;
import naturel.node.AAssignmentStatement;
import naturel.node.ABlock;
import naturel.node.AClass;
import naturel.node.ADeclaration;
import naturel.node.ADeclarationStatement;
import naturel.node.ADefaultType;
import naturel.node.ADivExp;
import naturel.node.ADotExp;
import naturel.node.AEqExp;
import naturel.node.AFnumExp;
import naturel.node.AGtExp;
import naturel.node.AGteqExp;
import naturel.node.AIdentifierExp;
import naturel.node.AIfStatement;
import naturel.node.AListExp;
import naturel.node.ALtExp;
import naturel.node.ALteqExp;
import naturel.node.AMethod;
import naturel.node.AMethodcallExp;
import naturel.node.AMethodcalldefExp;
import naturel.node.AMinusExp;
import naturel.node.AModExp;
import naturel.node.AMultExp;
import naturel.node.ANamedVariable;
import naturel.node.ANaturel;
import naturel.node.ANeqExp;
import naturel.node.ANoneExp;
import naturel.node.ANumExp;
import naturel.node.AOrExp;
import naturel.node.APlusExp;
import naturel.node.APrivModifier;
import naturel.node.AProtModifier;
import naturel.node.APublModifier;
import naturel.node.AStringExp;
import naturel.node.ATrueFlag;
import naturel.node.ATupleExp;
import naturel.node.ATupleType;
import naturel.node.ATypeType;
import naturel.node.AWhileStatement;
import naturel.node.AXorExp;
import naturel.node.PExp;
import naturel.node.PModifier;
import naturel.node.PStatement;
import naturel.node.PVariable;
import naturel.node.Start;
import naturel.node.TIdentifier;

public class GenCode extends DepthVarManager {
	/**
	 * Methoden
	 */
	private StringBuffer codeBuffer = new StringBuffer();
	
	/**
	 * Klassenv
	 */
	private StringBuffer classVarBuffer = new StringBuffer();
	 
	/**
	 * Einrückung
	 */
	private int indent = 0;
	
	/** 
	 * Beim nächsten Add einrücken
	 */
	private boolean jetzteinruecken = false;
	
	/**
	 * Umleiten von Initialisierungen auf anderen StringBuffer, wenn der Wert
	 * eine Klassenvariable ist
	 */
	private boolean staticInit = false;
	
	/**
	 * Aktuelle Klasse der Dot-Expression
	 */
	private String curDotClass = "";
	
	/**
	 * Wenn wir so ein Konstrukt haben:
	 *  Objekt.Methode(..)
	 *  Dann muss als erster Parameter (this) das Objekt übergeben werden.
	 *  So merken wir uns die Variable
	 */
	private String rememberVar = "";
	private VariableDef rememberVarDef = null;

	/**
	 * Die Hauptklasse, die das zu startende Main enthält.
	 */
	private String mainclass = "";
	
	private String headerdatei = "";
	
	private AClass currClass;
		
	/**
	 * Eingerückten Text zufügen
	 * @param s
	 */
	private void add(String s) {
		if (staticInit) {
			classVarBuffer.append(s);
		} else {
			if (jetzteinruecken) {
				for (int x = 0; x < indent; x++) {
					codeBuffer.append('\t');
				}
				jetzteinruecken = false;
			}
			codeBuffer.append(s);
			jetzteinruecken = s.contains("\n");
		}
	}
	
	/**
	 * Einen Hint nur adden, wenn es angegeben wurde
	 * @param s
	 */
	private void addHint(String s) {
		if (!Main.settings.getCleanCode()) {
			add(" /* " + s + " */ ");
		}
	}
	
	/**
	 * Durchlauf beginnen.
	 * @param node Der Start-Node des AST
	 * @param mainclass Die Hauptklasse, die das Main enthält, das gestartet werden soll 
	 */
	public void starte(Start node, String mainclass, String headerdatei) {
		this.mainclass = mainclass;
		this.headerdatei = headerdatei;
		this.generateCCodeNames = true;
		caseStart(node);
	}
	
	@Override
	public void inANaturel(ANaturel node) {
		codeBuffer.append(Helper.getFileHeader());
		codeBuffer.append("#include \"" + headerdatei + "\"\n\n");
	}
	
	@Override
	public void outANaturel(ANaturel node) {
		codeBuffer.append("\n\n");
		generateClassVarFiller();
		codeBuffer.append("int main(int argc, char* argv[]) {\n");
		codeBuffer.append("\tint i;\n");
		codeBuffer.append("#ifdef HANDLE_SIGSEGV\n");
		codeBuffer.append("\tstruct sigaction sa;\n");
		codeBuffer.append("#endif\n");
		codeBuffer.append("\tlist sargs;\n");
		codeBuffer.append("#ifdef HANDLE_SIGSEGV\n");
		codeBuffer.append("\tsa.sa_handler = sighandler;\n");
		codeBuffer.append("\tsa.sa_flags = 0;\n");
		codeBuffer.append("\tsigaction(SIGSEGV, &sa, NULL);\n");
		codeBuffer.append("#endif\n");
		if (classVarBuffer.length() > 0) {
			codeBuffer.append("\tfillClassVars();\n");
		}
		codeBuffer.append("\tsargs = list_new(NULL, NULL);\n");
		codeBuffer.append("\tfor (i = argc - 1; i >= 0; i--) {\n");
		codeBuffer.append("\t\tsargs = list_new((Object)str_new(argv[i]), sargs);\n");
		codeBuffer.append("\t}\n");
		codeBuffer.append("\treturn " + mainclass + "_main(sargs)->val;\n");
		codeBuffer.append("}");
	}
	
	/**
	 * Erzeugt die Funktion, die die Klassenvariablen initialisiert
	 */
	private void generateClassVarFiller() {
		if (classVarBuffer.length() > 0) {
			codeBuffer.append("void fillClassVars() {\n");
			codeBuffer.append(classVarBuffer);
			codeBuffer.append("}\n\n");
		}
	}

	/**
	 * Neue Klasse: Wir dazu schon die Methoden an, die den Platz für ein Objekt der Klasse
	 * allokieren
	 */
	@Override
	public void inAClass(AClass node) {
		super.inAClass(node);
		
		currClass = node;
		
		add(curClassName + " create" + curClassName + "() {\n");
		add("\t" + curClassName + " obj = ((" + curClassName +
				")getmem(sizeof(struct S" + curClassName + ")));\n");
		add("\treturn obj;\n");
		add("}\n\n");
	}
	
	/**
	 * Start einer Methode
	 */
	@Override
	public void inAMethod(AMethod node) {
		super.inAMethod(node);
		codeBuffer.append('\n');
	}
	
	/**
	 * Generiert die Parameter einer Methode
	 * @param node
	 */
	public void genMethodParams(AMethod node) {
		boolean erster = true;
		for (PVariable e : node.getParams()) {
			if (!erster) {
				codeBuffer.append(", ");
			} else {
				erster = !erster;
			}
			ANamedVariable var = (ANamedVariable)e;
			
			VariableDef varDef = curMethod.params.get(var.getName().toString().trim());
			if (varDef == null) {
				throw new RuntimeException("Internal Error: Parameter?!?");
			}

			String param = "";
			if (Main.settings.getCleanCode()) {
				param = varDef.typ + " " + varDef.cCodeName + " ";
			} else {
				param = varDef.typ + " " + varDef.cCodeName + " /* " + varDef.name + " */ ";
			}
			/*
			if (var.getType() instanceof ATupleType) {
				ATupleType type = (ATupleType)var.getType();
				if (type.getArray() instanceof ATrueFlag) {
					param += "[]";
				}
			} else if(var.getType() instanceof ATypeType) {
				ATypeType type = (ATypeType)var.getType();
				if (type.getArray() instanceof ATrueFlag) {
					param += "[]";
				}
			}*/
			codeBuffer.append(param);
		}
	}
	
	/**
	 * Inhalt einer Methode
	 */
	@Override
	public void caseAMethod(AMethod node) {
		inAMethod(node);
		
		if (curMethod.name.equals("new") && curMethod.statisch) {
			// Konstruktor
			genConstructor(node);
		} else {
			// Normale Methode
			
			// static-Schlüsselwort macht die Funktion Modul-lokal
			if (curMethod.sichtbarkeit == Sichtbarkeit.PRIVATE) {
				codeBuffer.append("static ");
			}
			
			// Rückgabetyp der Funktion
			node.getType().apply(this);
			codeBuffer.append(' ');

			// This-Parameter nur bei nicht-statischen Methoden
			codeBuffer.append(curMethod.cCodeName + "(");
			if (!curMethod.statisch) {
				codeBuffer.append(curClassName + " this");
				if (node.getParams() != null && node.getParams().size() >= 1) {
					codeBuffer.append(", ");
				}
			}
			genMethodParams(node);

			// Funktionskopf beenden und Methoden-Körper ausgeben
			codeBuffer.append(") ");
			indent++;
			add("{\n");
			node.getBody().apply(this);
			indent--;
			add("}\n");
		}
		outAMethod(node);
	}

	/**
	 * Erzeugt einen Konstruktor
	 */
	private void genConstructor(AMethod node) {
		boolean params = node.getParams() != null && node.getParams().size() > 0;
		
		add(curClassName + " " + curMethod.cCodeName + "(");
		genMethodParams(node);
		add(") {\n");
		
		// Wir müssen zunächst die Variablendeklarationen aus dem Funktionskörper ausgeben,
		// sonst ist es kein ANSI C.
		ABlock block = (ABlock)node.getBody();
		indent++;
		List<PStatement> declStatements = new ArrayList<PStatement>();
		for (PStatement s : block.getStatements()) {
			if (s instanceof ADeclarationStatement) {
				s.apply(this);
				declStatements.add(s);
				add("\n");
			} else {
				break;
			}
		}
		block.getStatements().removeAll(declStatements);
		indent--;
		
		// Nun das this-Objekt anlegen und initialisieren
		add("\t" + curClassName + " this = create" + curClassName + "();\n");
		add("\tinit" + curClassName + "(this");
		if (params) {
			add(", ");
		}
		boolean erster = true;
		for (PVariable e : node.getParams()) {
			if (!erster) {
				codeBuffer.append(", ");
			} else {
				erster = !erster;
			}
			ANamedVariable var = (ANamedVariable)e;
			//TODO: NullPointerExceptions abfangen (Sollten nicht auftreten, AAABER...)
			String realName = curMethod.params.get(var.getName().toString().trim()).cCodeName;
			add(realName);
		}
		add(");\n");

		// Sonstigen Konstruktor-Inhalt bauen
		indent++;
		ABlock body = (ABlock)node.getBody();
		jetzteinruecken = true;
		for (PStatement statement : body.getStatements()) {
			// Keine Exemplarvariablen-Initialisierungs-Assignment-Statements im
			// Konstruktor ausgeben, die müssen in die initKlasse-Methode!
			if (statement instanceof AAssignmentStatement
					&& ((AAssignmentStatement)statement).getId() instanceof AMethodcallExp
					&& ((AMethodcallExp)((AAssignmentStatement)statement).getId()).getId().getLine() == -2) {
				continue;
			}
			statement.apply(this);
			add(";\n");
		}
		indent--;
		add("\treturn this;\n");
		add("}\n\n");
		
		// Init-Methode erzeugen
		// Kopfzeile
		add("void init" + curClassName + "(" + curClassName + " this");
		if (params) {
			add(", ");
		}
		genMethodParams(node);
		add(") {\n");
		
		// Superkonstruktor-Init-Aufruf
		String superClass = Helper.listToString(currClass.getSuper());
		add("\tinit" + superClass + "((" + superClass + ")this);\n");
		
		// Methoden setzen
		ClassDef cdef = alleKlassenMethoden.get(curClassName);
		if (cdef == null) {
			throw new RuntimeException("Error in program: No definition for Class \"" + curClassName + "\"");
		}
		for (String m : cdef.methods.keySet()) {
			MethodenDef methode =  alleKlassenMethoden.get(curClassName).methods.get(m);
			if (!methode.statisch) {
				String methodContainer = Helper.classThatHasMethod(curClassName, methode.name);
				add("\t((" + methodContainer + ")this)->" + methode.name + " = "
						+ methode.cCodeName + ";\n");
			}
		}
		
		// Exemplarvariablen-Initialisierung muss auch in die Init-Methode,
		// sonst funktioniert das mit der Vererbung nicht
		indent++;
		for (PStatement s : block.getStatements()) {
			if (s instanceof AAssignmentStatement
					&& ((AAssignmentStatement)s).getId() instanceof AMethodcallExp
					&& ((AMethodcallExp)((AAssignmentStatement)s).getId()).getId().getLine() == -2) {
				s.apply(this);
				add(";\n");
			}
		}
		indent--;
		add("}\n\n");
	}
	
	@Override
	public void caseATypeType(ATypeType node) {
		if (node.getArray() != null && (node.getArray() instanceof ATrueFlag)) {
			add("list " + node.getName().toString().trim());
		} else {
			add(node.getName().toString().trim());
		}
	}
	
	@Override
	public void caseADefaultType(ADefaultType node) {
		codeBuffer.append("void");
	}
	
	@Override
	public void caseATupleType(ATupleType node) {
		// Wenn der Typ ein Tupel mit nur einem Element ist, dann ist dieses Element unser Typ.
		// Wir brauchen dafür nicht extra ein Tupel-Struct generieren.
		if (node.getVars().size() == 1) {
			node.getVars().get(0).apply(this);
			return;
		}
		
		codeBuffer.append("_tup_");
		boolean first = true;
		for (PVariable var : node.getVars()) {
			if (first) {
				first = false;
			} else {
				codeBuffer.append('_');
			}
			var.apply(this);
		}
	}

	@Override
	public void caseANamedVariable(ANamedVariable node) {
		inANamedVariable(node);
		VariableDef varDef = searchVariable(node.getName().toString().trim(), null);

		// Und ausgeben - wenn wirklich.
		boolean print = true;
		if (curMethod == null) {
			//Klassenvariable! Nur ausgeben, wenn das static ist!
			if (node.parent() instanceof ADeclaration) {
				PModifier modi = ((ADeclaration)node.parent()).getModifier();
				if ((modi instanceof APrivModifier) ||
						(modi instanceof APublModifier) ||
						(modi instanceof AProtModifier)) {
					print = false;
				}
			}
		} 
		if (print) {
			if (node.getType() != null && (node.getType() instanceof ATypeType)) {
				ATypeType aktnode = (ATypeType) node.getType();
				String result;
				if (aktnode.getArray() != null && aktnode.getArray() instanceof ATrueFlag) {
					result = "list " + varDef.cCodeName;
					if (!Main.settings.getCleanCode()) {
						result += " /*" + varDef.name + "*/";
					}
				} else {
					result = varDef.typ + " " + varDef.cCodeName;
					if (!Main.settings.getCleanCode()) {
						result += " /*" + varDef.name + " */";
					}
				}
				add(result);
				
				// Klassenvariable: Initialisierung zufügen
				if (varDef.statisch && !(node.getVal() instanceof ANoneExp)) {
					staticInit = true;
					add("\t" + varDef.cCodeName);
				}
			}

			if (node.getVal() != null && !(node.getVal() instanceof ANoneExp)) {
				add(" = ");
				node.getVal().apply(this);
			}
			
			if (staticInit) {
				add(";\n");
				staticInit = false;
				add(";\n");
			} else {
				add(";");
			}
		}
		
		outANamedVariable(node);
	}
	
	/**
	 * Zuweisung
	 */
    @Override
	public void caseAAssignmentStatement(AAssignmentStatement node) {
		inAAssignmentStatement(node);
		
		// Zuweisungen mit ANoneExp können auftreten wenn Deklarationen mit
		// initialen Zuweisungen zerlegt werden, die aber keinen Wert enthalten
		if (node != null && node.getVal() != null && node.getVal() instanceof ANoneExp) {
			return;
		}
		
		// Zuweisung an Variable
		String name = null;
		TIdentifier id = null;
		if (node.getId() instanceof AIdentifierExp) {
			AIdentifierExp exp = (AIdentifierExp)node.getId();
			id = exp.getId();
			name = id.toString().trim();
			
		}
		if (node.getId() instanceof AMethodcallExp) {
			AMethodcallExp exp = (AMethodcallExp)node.getId();
			id = exp.getId();
			name = id.toString().trim();
		}
		if (node.getId() instanceof AIdentifierExp || node.getId() instanceof AMethodcallExp) {
			VariableDef akt = searchVariable(name, null);
			if (akt == null) {
				throw new RuntimeException("Variable not defined [" + id.getLine()
						+ ", " + id.getPos() + "]: " + name);
			}
			if (akt.className != null) {
				add("((" + akt.className + ")this)->");
			}
			if (node.getId() != null) {
				if (Main.settings.getCleanCode()) {
					add(akt.cCodeName + " ");
				} else {
					add(akt.cCodeName + " /* " + akt.name + " */ ");
				}
			}
			curDotClass = "";
			add(" = ");
			if (node.getVal() != null) {
				node.getVal().apply(this);
			}
			outAAssignmentStatement(node);
			return;
		}
		
		// Zuweisung an Feld in Objekt
		if (node.getId() instanceof AListExp) {
			// TODO
			outAAssignmentStatement(node);
			return;
		}

		// Zuweisung an ein Tupel
		if (node.getId() instanceof ATupleExp) {
			throw new RuntimeException("Tuples are not implemented yet");
			//outAAssignmentStatement(node);
		}
		throw new RuntimeException("Assignment to unknown type " + node.getId().getClass().getSimpleName());
	}
	
	@Override
	public void caseAIfStatement(AIfStatement node) {
		inAIfStatement(node);
		String end = "ifEndLabel_" + Helper.getNextVarNum();

		add("if (!(");
		if (node.getCond() != null) {
			node.getCond().apply(this);
		}
		add(")) goto " + end + ";\n");
		if (node.getBlock() != null) {
			node.getBlock().apply(this);
		}
		add(end + ":\n");
		outAIfStatement(node);
	}
	
	@Override
	public void caseAWhileStatement(AWhileStatement node) {
		inAWhileStatement(node);
		String beg = "whileBeginLabel_" + Helper.getNextVarNum();
		String end = "whileEndLabel_" + Helper.getNextVarNum();
		
		add(beg + ":\n");
		add("if (!(");
		if (node.getCond() != null) {
			node.getCond().apply(this);
		}
		add(")) goto " + end + ";\n");
		
		if (node.getBlock() != null) {
			node.getBlock().apply(this);
		}
		add("goto " + beg + ";\n");
		add(end + ":\n");
		outAWhileStatement(node);
	}

	
	@Override
	public void inABlock(ABlock node) {
		super.inABlock(node);
	}

	@Override
	public void outABlock(ABlock node) {
		super.outABlock(node);
	}
	
	@Override
	public void caseABlock(ABlock node) {
		inABlock(node);
		{
			List<PStatement> copy = new ArrayList<PStatement>(node.getStatements());
			for (PStatement e : copy) {
				e.apply(this);
				curDotClass = "";
				if (e instanceof ADeclarationStatement) {
					// Variablendeklarationen haben schon ein ;
					add("\n");
				} else {
					if (!(e instanceof AIfStatement) && !(e instanceof AWhileStatement)) {
						// Ifs und Whiles brauchen kein Semikolon
						add(";\n");
					}
				}
				rememberVar = ""; // Neue Zeile, neues Glück..
			}
		}
		outABlock(node);
	}

	@Override
	public void caseAMethodcallExp(AMethodcallExp node) {
		inAMethodcallExp(node);
		if ((node.getArgs() == null) || (node.getArgs().size() <= 0)) {
			// Das ist eine Variable
			String name = node.getId().toString().trim();
			VariableDef aktVarDef = searchVariable(name, null);
			if (aktVarDef == null) {
				throw new RuntimeException("Undefinded variable [" + node.getId().getLine() + 
						", " + node.getId().getPos() + "]: " +name);
			}
			if ((aktVarDef.className != null) && (!aktVarDef.className.equals("")
					&& !aktVarDef.statisch)) {
				// this-> nicht bei statischen Methoden und Variablen zufügen
				add("this->");
			}
			add(aktVarDef.cCodeName);
			addHint(name);
			curDotClass = aktVarDef.typ;// TODO: Auch im anderen IF-Zweig
			if (curDotClass == null) {
				throw new RuntimeException("Error in Program: typ not defined: " + name);
			}
			rememberVar = aktVarDef.name;
		} else {
			// Methode
			MethodenDef zielmethode = null;
			if (!curDotClass.equals("")) {
				// Das MUSS da drin stehen!
				zielmethode = searchMethode(node.getId().toString().trim(), curDotClass);
			}  else {
				zielmethode = searchMethode(node.getId().toString().trim(), null);
				//if (zielmethode == null) {
				//}
			}
			String methodenName = "?";
			String zielklasse = "";
			if (zielmethode == null) {
				// Nö. Hilfe!
				throw new RuntimeException("Invalid method call [" + node.getId().getLine()
						+ "," + node.getId().getPos() + "]: Method not found: " + node.getId()
						+ " (should be in class " + curDotClass + ")");
			}
			if (zielmethode.statisch) {
				add(zielmethode.cCodeName);
			} else	{
				if (rememberVarDef != null) {
					add("((" + zielklasse + ")" + rememberVarDef.cCodeName + ")->");
					rememberVarDef = null;
				} else if (!zielmethode.className.equals("")) {
					// irgendwas geerbtes
					add("((" + zielmethode.className + ")this)->");
				}
				add(zielmethode.name);
			}
			curDotClass = ""; // Wird nicht weiter gebraucht
			methodenName = zielmethode.cCodeName;
			if (node.getArgs().size() > 0) {
				if (!methodenName.toLowerCase().equals("return")) {
					// Return geht anders!
					add("(");
				} else {
					add(" ");
				}
				boolean erster = true;
				if (!zielmethode.statisch) {
					// Hier muss ein Link zum aktuellen Objekt rein
					if (!rememberVar.equals("")) {
						VariableDef vdef = searchVariable(rememberVar, null);
						if (vdef == null) {
							System.out.println(codeBuffer);
							throw new RuntimeException("Error in Program: Variable " + rememberVar + " not found.");
						}
						add(vdef.cCodeName);
						rememberVar = "";
					} else {
						add("(" + zielmethode.className + ")this");
					}
					erster = false;
				}
				List<PExp> copy = new ArrayList<PExp>(node.getArgs());
				if (!(copy.get(0) instanceof ANoneExp)) {
					for (PExp e : copy) {
						if (!erster) {
							add(", ");
						} else {
							erster = !erster;
						}
						e.apply(this);
					}
				}
				if (!methodenName.toLowerCase().equals("return")) {
					// Return geht anders!
					add(")");
				}
			}
		}
		outAMethodcallExp(node);
	}
	
	@Override
	public void caseAPlusExp(APlusExp node) {
		inAPlusExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" + ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAPlusExp(node);
	}
	
	@Override
	public void outStart(Start node) {
		codeBuffer.append("\n");
	}
	
	public StringBuffer getContent() {
		return codeBuffer;
	}

	@Override
	public void caseAMinusExp(AMinusExp node) {
		inAMinusExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" - ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAMinusExp(node);
	}
	
	@Override
	public void caseADivExp(ADivExp node) {
		inADivExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" / ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outADivExp(node);
	}
	
	@Override
	public void caseAMultExp(AMultExp node) {
		inAMultExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" * ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAMultExp(node);
	}
	
	@Override
	public void caseAModExp(AModExp node) {
		inAModExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" % ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAModExp(node);
	}
	
	@Override
	public void caseAAndExp(AAndExp node) {
		inAAndExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" && ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAAndExp(node);
	}
	
	@Override
	public void caseAOrExp(AOrExp node) {
		inAOrExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" || ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAOrExp(node);
	}
	
	@Override
	public void caseAXorExp(AXorExp node) {
		inAXorExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" ^ ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAXorExp(node);
	}
	
	@Override
	public void caseAEqExp(AEqExp node) {
		inAEqExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" == ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAEqExp(node);
	}

	@Override
	public void caseANeqExp(ANeqExp node) {
		inANeqExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" != ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outANeqExp(node);
	}
	
	@Override
	public void caseALteqExp(ALteqExp node) {
		inALteqExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" <= ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outALteqExp(node);
	}
	
	@Override
	public void caseALtExp(ALtExp node) {
		inALtExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" < ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outALtExp(node);
	}
	
	@Override
	public void caseAGteqExp(AGteqExp node) {
		inAGteqExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" >= ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAGteqExp(node);
	}
	
	@Override
	public void caseAGtExp(AGtExp node) {
		inAGtExp(node);
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" > ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
		outAGtExp(node);
	}
	
	@Override
	public void caseANumExp(ANumExp node) {
		inANumExp(node);
		if (node.getNum() != null) {
			add(node.toString().trim());
			node.getNum().apply(this);
		}
		outANumExp(node);
	}
	
	@Override
	public void caseAFnumExp(AFnumExp node) {
		inAFnumExp(node);
		if (node.getFnum() != null) {
			add(node.toString().trim());
			node.getFnum().apply(this);
		}
		outAFnumExp(node);
	}
	
	@Override
	public void caseAStringExp(AStringExp node) {
		inAStringExp(node);
		if (node.getStr() != null) {
			add(node.getStr().toString().trim());
			node.getStr().apply(this);
		}
		outAStringExp(node);
	}
	
	@Override
	public void caseAIdentifierExp(AIdentifierExp node) {
		inAIdentifierExp(node);
		if (node.getId() != null) {
			node.getId().apply(this);
		}
		outAIdentifierExp(node);
	}
	
    /**
     * Variablendeklaration innerhalb von Methode
     */
    @Override
	public void caseADeclarationStatement(ADeclarationStatement node) {
		inADeclarationStatement(node);
		if (node.getVar() != null) {
			node.getVar().apply(this);
		}
		outADeclarationStatement(node);
	}
	
    @Override
	public void caseAPrivModifier(APrivModifier node) {
		inAPrivModifier(node);
		outAPrivModifier(node);
	}
    
    @Override
	public void caseAMethodcalldefExp(AMethodcalldefExp node) {
        inAMethodcalldefExp(node);
    	// Vorne steht die Klasse, dahinter die Methode/Variable
    	if (! (node.getClazz() instanceof AMethodcallExp)) {
    		throw new RuntimeException("Error in program: not the correct instanceof: " + node.getClazz().getClass().getSimpleName());
    	}
    	if (! (node.getMethod() instanceof AMethodcallExp)) {
    		throw new RuntimeException("Error in program: not the correct instanceof: " + node.getMethod().getClass().getSimpleName());
    	}
    	String clazz = ((AMethodcallExp) node.getClazz()).getId().toString().trim();
    	AMethodcallExp meth = (AMethodcallExp) node.getMethod();
    	if ((meth.getArgs() == null) || (meth.getArgs().size() <= 0)) {
    		// Variable
    		VariableDef vdef = searchVariable(meth.toString().trim(), clazz);
    		if (vdef == null) {
    			throw new RuntimeException("Error in program: ClassVar not found: " + clazz + " / " + meth.getId().toString().trim());
    		}
    		add(vdef.cCodeName);
    		addHint(vdef.name);
    		curDotClass = vdef.typ; 
    	} else {
    		// Methode
    		MethodenDef mdef = searchMethode(meth.getId().toString().trim(), clazz);
    		if (mdef == null) {
    			throw new RuntimeException("Error in program: ClassMethod not found: " + clazz + " / " + meth.getId().toString().trim());
    		}
    		add(mdef.cCodeName);
    		addHint(mdef.name);
    		add("(");
    		
    		boolean erster = true;
			for (PExp e : meth.getArgs()) {
				if (!erster) {
					add(", ");
				} else {
					erster = !erster;
				}
				e.apply(this);
			}
    		add(")");
    		//curDotClass = mdef.rettype; 
    	}
        outAMethodcalldefExp(node);
    }
    
    
    /**
     * Hier wurden alle "Dot" flachgeklopft.
     */
    @Override
    public void caseAListExp(AListExp node) {
        inAListExp(node);
        /*
        {
        	List<PExp> copy = new ArrayList<PExp>(node.getList());
        	boolean erster = true;
            for(PExp e : copy) {
            	if (e instanceof AMethodcalldefExp) {
            		e.apply(this);
            	} else /*
            		AMethodcalldefExp mcd = (AMethodcalldefExp)e;
            		AMethodcallExp typedNode = (AMethodcallExp)mcd.getMethod();
            		if (typedNode.getArgs() != null) {
            			curDotClass = mcd.getClazz().toString().trim();
            			mcd.getMethod().apply(this);
            			curDotClass = "";
            		} else {
            			
            		}
            	} else if (e instanceof AMethodcallExp) {
            		AMethodcallExp typedNode = (AMethodcallExp)e; 
            		
            		typedNode.apply(this);
            	} else * /if (e instanceof AMethodcallExp) {
            		AMethodcallExp typedNode = (AMethodcallExp)e; 
            		String name = typedNode.getId().toString().trim();
            		
            		// Alle was sein kann, wenn vorher schon eine Klasse geschrieben wurde:
            		
            		if (!curDotClass.equals("")) {
                    	// Zuerst: Methode auf einer Klasse:
            			if (typedNode.getArgs() != null) {
            				e.apply(this);
            				curDotClass = "";
            			} else {
                        	// Variable einer Klasse?
                			VariableDef var = searchVariable(name, curDotClass);
            				
                			if (var != null) {
                				// Das kann nur eine public static sein. Daher:
                				add(var.cCodeName);
                				addHint(curDotClass + " " + name);
                			} else {
                				// war das curDotClass nur ein Packagename, und das jetzt ist die Klasse?
                				String searchName = curDotClass + "_" + name;
                				boolean found = false;
                				for (String s : alleKlassenMethoden.keySet()) {
                					if (s.startsWith(searchName)) {
                        				// Klasse existiert
                        				curDotClass = searchName;
                        				found = true;
                					}
                				}
                				if (!found) {
                					throw new RuntimeException("Could not identify Identifier ["
                							+ typedNode.getId().getLine() + ", " + typedNode.getId().getPos()
                							+ "] : " + name + "(" + searchName + ")");
                				}
                			}
            			}
            		} else {
                    	// Es gibt noch keine Informationen auf dem "Klassen-Stack": curDotClass
            			// Ist das eine Methode
            			if ((typedNode.getArgs() != null) && (typedNode.getArgs().size() > 0)) {
            				e.apply(this);
            				curDotClass = "";
            			} else {
            				// Ist das eine Variable?
            				VariableDef var = searchVariable(name, null);
            				if (var != null) {
            					rememberVarDef = var;
            					//if (Main.settings.getCleanCode()) {
            					//	add(var.cCodeName + "->");
            					//} else {
            					//	add(var.cCodeName + " /* " + name + " *x/ ->");
            					//}
            					rememberVar = var.name; 
            				} else {
            					// Ist das eine Klasse?
            				
                				String searchName = name;
                				boolean found = false;
                				for (String s : alleKlassenMethoden.keySet()) {
                					if (s.startsWith(searchName)) {
                        				// Klasse existiert
                        				curDotClass = searchName;
                        				found = true;
                					}
                				}
                				if (!found) {
                					throw new RuntimeException("Could't identify Identifier ["
                							+ typedNode.getId().getLine() + ", "
                							+ typedNode.getId().getPos() + "] : " + name);
                				}
            				}
            			}
            		} // else von: if (!curDotClass.equals("")) { 
            	} // if (e instanceof AMethodcallExp)  
            } // for 
        	curDotClass = "";
        	rememberVar = "";
        	rememberVarDef = null;
        }
        */
        outAListExp(node);
    }

    @Override
    public void caseADotExp(ADotExp node) {
        inADotExp(node);
        // Aufgrund der Problematik, das auch L von R abhängt, kann man hier nicht einfach
        // in die Visitoren gehen...
        
        // Zuerst alle Defs holen 
        //MethodenDef mL = null; // Links darf keine Methode stehen
        VariableDef vL = null;
        MethodenDef mR = null;
        VariableDef vR = null;
        AMethodcallExp methMethR = null;
        
        if (node.getL() instanceof AMethodcalldefExp) {
        	AMethodcalldefExp mde = (AMethodcalldefExp) node.getL();
        	if (! (mde.getClazz() instanceof AMethodcallExp)) {
        		throw new RuntimeException("Error in Program: Node instanceof " + mde.getClazz().getClass().getSimpleName());
        	}
        	AMethodcallExp methClass = (AMethodcallExp) mde.getClazz();
        	String cla = methClass.getId().toString().trim();
        	
        	if (! (mde.getMethod() instanceof AMethodcallExp)) {
        		throw new RuntimeException("Error in Program: Node instanceof " + mde.getMethod().getClass().getSimpleName());
        	}
        	AMethodcallExp methMeth = (AMethodcallExp) mde.getMethod();
        	String name = methMeth.getId().toString().trim();
        	
        	if ((methMeth.getArgs() == null) || (methMeth.getArgs().size() <= 0)) {
        		// Variable
        		vL = searchVariable(name, cla);
        		if (vL == null) {
        			throw new RuntimeException("Error in Program: VariablenDef not found: " + cla + " . " + name);
        		}
        	} else {
        		// Methode - darf nicht sein
       			throw new RuntimeException("Error in Program: Left at DOT isn't a Method not allowed [" + methMeth.getId().getLine() + "," + methMeth.getId().getPos());
        	}
        } else if (node.getL() instanceof AMethodcallExp) {
        	AMethodcallExp methMeth = (AMethodcallExp) node.getL();
        	String name = methMeth.getId().toString().trim();
        	if ((methMeth.getArgs() == null) || (methMeth.getArgs().size() <= 0)) {
        		// Variable
        		vL = searchVariable(name, null);
        		if (vL == null) {
        			throw new RuntimeException("Error in Program: VariablenDef not found: " + name);
        		}
        	} else {
        		// Methode - darf nicht sein
       			throw new RuntimeException("Error in Program: Left at DOT isn't a Method not allowed [" + methMeth.getId().getLine() + "," + methMeth.getId().getPos());
        	}
        } else {
        	throw new RuntimeException("Error in Program: getL() instanceof " + node.getL().getClass().getSimpleName());
        }
        
        if (node.getR() instanceof AMethodcallExp) {
        	methMethR = (AMethodcallExp) node.getR();
        	String name = methMethR.getId().toString().trim();
        	if ((methMethR.getArgs() == null) || (methMethR.getArgs().size() <= 0)) {
        		// Variable
        		vR = searchVariable(name, vL.typ);
        		if (vR == null) {
        			throw new RuntimeException("Error in Program: VariablenDef not found: " + vL.typ + " . " + name);
        		}
        	} else {
        		// Methode
        		mR = searchMethode(name, vL.typ);
        		if (mR == null) {
        			throw new RuntimeException("Error in Program: MethodenDef not found: " + vL.typ +" . " + name);
        		}
        	}
        } else {
        	throw new RuntimeException("Error in Program: getR() instanceof " + node.getR().getClass().getSimpleName());
        }
        
        // Was kann auftreten:
        // V->V
        // V->M
    	String klasse = null;
    	String name = null;
        if (vR != null) {
        	name = vR.name;
        	// Klasse rausfinden, von wo das kommt (wenn geerbt)
        	ClassDef cdef = alleKlassenMethoden.get(vR.className);
        	while (cdef != null) {
        		if ((vR != null) && (cdef.vars.get(name) != null)) {
        			klasse = cdef.name;
        		}
        		cdef = alleKlassenMethoden.get(cdef.superof);
        	}
        } else {
        	name = mR.name;
        	// Klasse rausfinden, von wo das kommt (wenn geerbt)
        	ClassDef cdef = alleKlassenMethoden.get(mR.className);
        	while (cdef != null) {
        		if ((mR != null) && (cdef.methods.get(name) != null)) {
        			klasse = cdef.name;
        		}
        		cdef = alleKlassenMethoden.get(cdef.superof);
        	}
        }
        if (vR != null) {
        	// Rechts steht eine Variable
        	//auf die Klasse casten
        	// Aber suchen, von welcher Vererbunghirarchie das kommt.
        	add("((" + klasse + ") " + vL.cCodeName);
        	addHint(vL.name);
        	add(")->" + vR.cCodeName);
        	addHint(vR.name);
        } else {
        	// Rechts steht eine Methode
        	add("((" + klasse + ") " + vL.cCodeName);
        	addHint(vL.name);
        	add(")->" + mR.name);
        	addHint(mR.name);
        	add("(");
        	boolean erster = true;
        	if (!mR.statisch) {
        		// this-Parameter
        		add("(" + mR.className + ") "+ vL.cCodeName);
            	addHint("this");
            	erster = false;
        	}
        	List<PExp> copy = new ArrayList<PExp>(methMethR.getArgs());
        	if (!(copy.get(0) instanceof ANoneExp)) {
        		for (PExp e : copy) {
        			if (erster) {
        				erster = false;
        			} else {
        				add(", ");
        			}
        			e.apply(this);
        		}
        	}
        	add(")");
        }
        
        
/*        if(node.getL() != null) {
            node.getL().apply(this);
        }
        add("->");
        if(node.getR() != null) {
            node.getR().apply(this);
        }*/
        curDotClass = "";
        outADotExp(node);
    }

}

