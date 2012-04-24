package naturel.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import naturel.Main;
import naturel.generator.ILRNode;
import naturel.generator.SammleKlassenMethoden.ClassDef;
import naturel.generator.SammleKlassenMethoden.MethodenDef;
import naturel.generator.definitions.VariableDef;
import naturel.node.ADefaultType;
import naturel.node.AListExp;
import naturel.node.AMethod;
import naturel.node.AMethodcallExp;
import naturel.node.AMethodcalldefExp;
import naturel.node.ANamedVariable;
import naturel.node.ANumExp;
import naturel.node.AStringExp;
import naturel.node.ATrueFlag;
import naturel.node.ATupleType;
import naturel.node.ATypeType;
import naturel.node.AUnnamedVariable;
import naturel.node.PExp;
import naturel.node.PType;
import naturel.node.PVariable;
import naturel.node.TIdentifier;

public class Helper {
	/**
	 * Speichert alle Methodennamen zu den Klassen
	 */
	private static Map<String, ClassDef> allClasses = null;
	
	/**
	 * Zum generieren der Variablen-Nummern
	 */
	private static int varNum = 0;
	
	/**
	 * Übergeben des Objektes, das alle Klassen und Methoden enthält
	 */
	public static void setAlleKlassenMethoden(Map<String, ClassDef> alleKlassenMethoden) {
		allClasses = alleKlassenMethoden;
	}
	
	public static int getNextVarNum() {
		varNum += 1;
		return varNum;
	}
	
	/**
	 * Verwandelt eine Identifier-Liste in einen String, der einen Underscore als Trenner hat
	 */
	public static String listToString(LinkedList<TIdentifier> list) {
		String result = list.getFirst().getText().trim();
		for (int i = 1; i < list.size(); i++) {
			result += "_" + list.get(i).getText().trim();
		}
		return result;
	}

	/**
	 * Generierter Dateiheader
	 */
	public static String getFileHeader() {
		String result = "/* Generated by Naturel. Do not modify. */\n";
		result += "/* " + Main.VERSION + " - " + new Date() + " */\n\n";
		return result;
	}

	/**
	 * Liefert die Stringdarstellung eines Types
	 */
	public static String typeToString(PType type) {
		if (type instanceof ATupleType) {
			ATupleType node = (ATupleType)type;
			String className = "_tup";
			for (PVariable var : node.getVars()) {
				className += "_";
				if (var instanceof ANamedVariable) {
					ANamedVariable v = (ANamedVariable)var;
					String typeName = v.getType().toString().trim();
					className += typeName;
				} else if (var instanceof AUnnamedVariable) {
					AUnnamedVariable v = (AUnnamedVariable)var;
					String typeName = v.getType().toString().trim();
					className += typeName;
				}
			}
			return className;
		} else if (type instanceof ADefaultType) {
			return "void";
		} else if (type instanceof ATypeType) {
			ATypeType t = (ATypeType)type;
			String result;
			if (t.getArray() != null && t.getArray() instanceof ATrueFlag) {
				result = "list";
			} else {
				result = t.getName().toString().trim();
			}
			return result;
		}
		return "";
	}
	
	/**
	 * Hängt die Liste der Parameter einer Methode an einen Stringbuffer 
	 */
	public static void genMethodParams(AMethod node, MethodenDef mdef, StringBuffer methodOut) {
		boolean erster = true;
		for (PVariable e : node.getParams()) {
			if (!erster) {
				methodOut.append(", ");
			} else {
				erster = !erster;
			}
			ANamedVariable var = (ANamedVariable)e;
			VariableDef vdef =  mdef.params.get(var.getName().toString().trim());
			
			String param = vdef.typ + " " + vdef.cCodeName; 
				//Helper.typeToString(var.getType()) + " " + var.getName().toString().trim();
			if (var.getType() instanceof ATupleType) {
				ATupleType type = (ATupleType)var.getType();
				if (type.getArray() instanceof ATrueFlag) {
					//param += "[]";
					param = "list"; //TODO: HÄ?!? Ist das richtig?
				}
			} else if(var.getType() instanceof ATypeType) {
				ATypeType type = (ATypeType)var.getType();
				if (type.getArray() instanceof ATrueFlag) {
					//param += "[]";
					param = "list"; //TODO: HÄ?!? Ist das richtig?
				}
			}
			methodOut.append(param);
		}
	}
	
	/**
	 * Liefert den Namen der Klasse, die eine bestimmte Methode hat, und zuoberst in der
	 * Vererbungshierarchie liegt
	 */
	public static String classThatHasMethod(final String cls, final String meth) {
		String result = "Object";
		String clazz = cls;
		
		if (meth.equals("tostr")) {
			return result;
		}
		while (!clazz.equals("Object")) {
			ClassDef cdef = allClasses.get(clazz);
			if (cdef == null) {
				throw new RuntimeException("Unknown Class " + clazz);
			}

			if (cdef.methods.get(meth) != null) {
				result = clazz;
			}
			
			String works = clazz;
			clazz = cdef.superof;
			if (clazz == null) {
				return works;
			}
		}
		
		return result;
	}
	
	//TODO: Kann gelöscht werden
	public static String typeOf(PExp exp) {
		if (exp instanceof AStringExp) {
			return "str";
		}
		if (exp instanceof ANumExp) { 
			return "num";
		}
		if (exp instanceof ILRNode) {
			return typeOf(((ILRNode)exp).getL());
		}
		if (exp instanceof AMethodcallExp) {
			AMethodcallExp meth = (AMethodcallExp)exp;
			if (meth.getArgs() != null && meth.getArgs().size() > 0) {
								
			}
		}
		return "";
	}
	
	public static PExp clonePExp(PExp exp) {
		PExp result = null;
		if (exp instanceof AListExp) {
			List<PExp> newList = new ArrayList<PExp>();
			for (PExp e : ((AListExp)exp).getList()) {
				newList.add(clonePExp(e));
			}
			result = new AListExp(newList);
		} else if (exp instanceof AMethodcallExp) {
			AMethodcallExp e = (AMethodcallExp)exp;
			List<PExp> cloneList = new ArrayList<PExp>();
			for (PExp ex : e.getArgs()) {
				cloneList.add(clonePExp(ex));
			}
			AMethodcallExp copy = new AMethodcallExp((TIdentifier)e.getId().clone(), cloneList);
			result = copy;
		} else if (exp instanceof AMethodcalldefExp) {
			AMethodcalldefExp e = (AMethodcalldefExp)exp;
			result = new AMethodcalldefExp(clonePExp(e.getClazz()), clonePExp(e.getMethod()));
		} else {
			result = (PExp)exp.clone();
		}
		return result;
	}
}
