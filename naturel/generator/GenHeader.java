package naturel.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import naturel.analysis.DepthFirstAdapter;
import naturel.generator.SammleKlassenMethoden.ClassDef;
import naturel.generator.SammleKlassenMethoden.MethodenDef;
import naturel.generator.definitions.VariableDef;
import naturel.helpers.Helper;
import naturel.node.AClass;
import naturel.node.ADeclaration;
import naturel.node.AMethod;
import naturel.node.ANamedVariable;
import naturel.node.ANaturel;
import naturel.node.ATupleType;
import naturel.node.AUnnamedVariable;
import naturel.node.PVariable;

/**
 * GenHeader erzeugt für einen Programm-AST die Headerdatei. Diese ist folgendermaßen aufgebaut:
 * 1) Makroguard öffnen
 * 2) Includes
 * 3) typedefs für Klassen (ermöglicht zyklische Abhängigkeiten)
 * 4) Klassendefinitionen als structs
 * 5) Klassendefinitionen der Tupel
 * 6) Makroguard schliessen
 * Der Inhalt der Datei ist in 4 Sektionen aufgeteilt, die jeweils von einem StringBuffer
 * dargestellt werden, und die unabhängig voneinander wachsen können: Die erste Sektion enthält Punkte
 * 1-3, die zweite Sektion enthält Punkt 4, die dritte Sektion enthält Punkt 5, die vierte Punkt 6.
 */
public class GenHeader extends DepthFirstAdapter {
	/**
	 * Set, das die Namen der Klassen für Tupel enthält, damit diese nicht mehrfach erzeugt werden
	 */
	private Set<String> tupleNames = new HashSet<String>();
		
	/**
	 * Kopf der Datei: Makroguard, typedefs
	 */
	private StringBuffer section1 = new StringBuffer();
	
	/**
	 * Körper: Structs
	 */
	private StringBuffer section2 = new StringBuffer();
	
	/**
	 * Körper: Funktionen für die static - Methoden
	 */
	private StringBuffer section2b = new StringBuffer();

	/**
	 * Körper: Structs für Tupel-Klassen
	 */
	private StringBuffer section3 = new StringBuffer();
	
	/**
	 * Fuss: Abschliessende Direktive von Makroguard
	 */
	private StringBuffer section4 = new StringBuffer();
	
	/**
	 * Aktueller Klassenname
	 */
	private String curClassName = "";
	
	/**
	 * Liste aller Klassen und Methoden
	 */
	private Map<String, ClassDef> alleKlassenMethoden = null;
	
	/**
	 * Aktuelles KlassenObjekt
	 */
	private ClassDef curClassDef = null; 
	
	/**
	 * Übergeben des Objektes, das alle Klassen und Methoden enthält
	 * @param alleKlassenMethoden
	 */
	public void setAlleKlassenMethoden(Map<String, ClassDef> alleKlassenMethoden) {
		this.alleKlassenMethoden = alleKlassenMethoden;
	}

	/**
	 * Wurzel des AST: Wir generieren Makroguard und includes, sowie grundlegende typedefs.
	 */
	@Override
	public void inANaturel(ANaturel node) {
		section1.append(Helper.getFileHeader());
		section1.append("#ifndef _NATUREL_H\n");
		section1.append("#define _NATUREL_H\n");
		section1.append("#include \"Core.h\"\n");
		section1.append("\n");
	}

	/**
	 * Ende des Programms: Schliessende Direktive für Makroguard
	 */
	@Override
	public void outANaturel(ANaturel node) {
		section4.append("#endif\n");
	}

	/**
	 * Start einer Klasse: typedef zu Sektion 1 zufügen (die später vor den eigentlichen structs
	 * erscheint), um zyklische Abhängigkeiten möglich zu machen), sowie das struct der Klasse in
	 * Sektion 2.
	 */
	@Override
	public void inAClass(AClass node) {
		curClassName = Helper.listToString(node.getName());
		curClassDef = alleKlassenMethoden.get(curClassName);
		if (curClassDef == null) {
			throw new RuntimeException("Collecting of methods and classes failed");
		}

		section1.append("typedef struct S" + curClassName + "* " + curClassName + ";\n");
		
		section2.append("struct S" + curClassName + " {\n");
		section2.append("\tstruct S" + Helper.listToString(node.getSuper()) + " super;\n");

	}

	/**
	 * Ende einer Klasse: struct schliessen.
	 */
	@Override
	public void outAClass(AClass node) {
		section2.append("};\n\n");
		section2b.append(curClassName + " create" + curClassName + "();\n");
	}

	/**
	 * Start einer Methode: 
	 */
	@Override
	public void inAMethod(AMethod node) {
		super.inAMethod(node);
		String name =  node.getName().toString().trim();
		MethodenDef curMethod = curClassDef.methods.get(name);
		
		if (curMethod == null) {
			throw new RuntimeException("Collecting of methods and classes failed");
		}
		
		if (curMethod.name.equals("new") && curMethod.statisch) {
			// Wir haben einen Konstruktor
			section2b.append(curClassName + " " + "new" + curClassName + "(");
			Helper.genMethodParams(node, curMethod, section2b);
			section2b.append(");\n");
			section2b.append("void init" + curClassName + "(" + curClassName + " this");
			if (node.getParams() != null && node.getParams().size() > 0) {
				section2b.append(", ");
			}
			Helper.genMethodParams(node, curMethod, section2b);
			section2b.append(");\n");
		} else {
			// Normale Methode
			if (!curMethod.statisch) {
				// Funktionsprotoyp erzeugen
				section2b.append(curMethod.rettype + " " + curMethod.cCodeName + "(" + curClassName + " this");
				if (node.getParams() != null && node.getParams().size() >= 1) {
					section2b.append(", ");
				}
				Helper.genMethodParams(node, curMethod, section2b);
				section2b.append(");\n");
				
				// Prototyp in struct nur erzeugen, wenn Methode nicht überschrieben ist
				if (Helper.classThatHasMethod(curClassName, curMethod.name).equals(curClassName)) {
					section2.append("\t" + curMethod.rettype + " (*" + curMethod.name + ")(" + curClassName + " this");
					if (node.getParams() != null && node.getParams().size() >= 1) {
						section2.append(", ");
					}
					Helper.genMethodParams(node, curMethod, section2);
					section2.append(");\n");
				}
			} else {
				// Für statische Methoden nur einen externen Prototyp ohne this-Parameter
				section2b.append(curMethod.rettype + " " + curMethod.cCodeName + "(");
				Helper.genMethodParams(node, curMethod, section2b);
				section2b.append(");\n");
			}
		}
	}
	
	@Override
	public void inADeclaration(ADeclaration node) {
		ANamedVariable var = (ANamedVariable)node.getVar();
		String name = var.getName().getText().trim();
		VariableDef curvar = curClassDef.vars.get(name);
		if (curvar == null) {
			throw new RuntimeException("Internal Error: No Variable-Definition in collect-Object");
		}
		if (curvar.statisch) {
			section4.append("extern " + curvar.typ + " " + curvar.cCodeName + ";\n");
		} else {
			section2.append("\t" + curvar.typ + " " + curvar.cCodeName + ";\n");
		}
	}
	
	/**
	 * Es wird ein Tupel verwendet. Wir schauen nach ob ein Typ für dieses Tupel definiert wurde,
	 * wenn nicht, fügen wir einen zu. Der Tupel-Typ soll heissen: _tup_X1_X2_... wobei Xn jeweils
	 * der Typ der List der Inhaltvariablen ist. Der Name der jeweiligen Liste wird nicht benötigt,
	 * weil zum einen ein Tupel nur durch die Typen seiner Komponenten definiert ist, zum Anderen
	 * auch anonyme Tupel verwendet werden können (bei denen eine oder meherere Komponenten keinen
	 * Namen haben).
	 */
	@Override
	public void caseATupleType(ATupleType node) {
		inATupleType(node);
		
		List<String> typeNames = new ArrayList<String>();
		String className = "_tup";
		for (PVariable var : node.getVars()) {
			className += "_";
			if (var instanceof ANamedVariable) {
				ANamedVariable v = (ANamedVariable)var;
				String typeName = v.getType().toString().trim();
				className += typeName;
				typeNames.add(typeName);
			} else if (var instanceof AUnnamedVariable) {
				AUnnamedVariable v = (AUnnamedVariable)var;
				String typeName = v.getType().toString().trim();
				className += typeName;
				typeNames.add(typeName);
			}
		}
		
		if (tupleNames.contains(className)) {
			return;
		}
			
		tupleNames.add(className);
		section1.append("typedef struct S" + className + "* " + className + ";\n");
		
		section3.append("struct S" + className + " {\n");
		int counter = 0;
		for (String s : typeNames) {
			section3.append("\t" + s + " _c" + counter + ";\n");
			counter++;
		}
		outATupleType(node);
	}
	
	@Override
	public void outATupleType(ATupleType node) {
		section3.append("};\n\n");
	}

	public StringBuffer getContent() {
		StringBuffer result = new StringBuffer();
		result.append(section1);
		result.append(section2);
		result.append(section2b);
		result.append(section3);
		result.append(section4);
		return result;
	}
}
