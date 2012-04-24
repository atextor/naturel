package naturel.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import naturel.analysis.DepthFirstAdapter;
import naturel.node.AAssignmentStatement;
import naturel.node.AClass;
import naturel.node.ADeclaration;
import naturel.node.ADefaultType;
import naturel.node.ADotExp;
import naturel.node.AMethod;
import naturel.node.AMethodcallExp;
import naturel.node.ANamedVariable;
import naturel.node.ANaturel;
import naturel.node.ANoneExp;
import naturel.node.APublStatModifier;
import naturel.node.ATrueFlag;
import naturel.node.ATupleExp;
import naturel.node.ATypeType;
import naturel.node.AUnnamedVariable;
import naturel.node.PExp;
import naturel.node.PVariable;

public class TypeChecker extends DepthFirstAdapter {
	private StringBuffer messages = new StringBuffer();
	private AClass curClass;
	private Map<AClass, List<String>> methods = new HashMap<AClass, List<String>>();
	private boolean mainFound = false;
	
	private void add(String s) {
		messages.append("Inconsistency found: " + s + "\n");
	}
	
	@Override
	public void inADeclaration(ADeclaration node) {
		PVariable var = node.getVar();
		if (var instanceof AUnnamedVariable) {
			AUnnamedVariable uv = (AUnnamedVariable)var;
			add("Unnamed variable");
			
			if (uv.getType() instanceof ADefaultType) {
				add("Type missing in declaration");
			}
		} else {
			ANamedVariable nv = (ANamedVariable)var;
			if (nv.getType() instanceof ADefaultType) {
				add("Type missing in declaration");
			}
			if (nv.getVal() instanceof ANoneExp) {
				return;
			}
			if (nv.getVal() instanceof AMethodcallExp) {
				AMethodcallExp m = (AMethodcallExp)nv.getVal();
				String name = m.getId().toString().trim();
				if (!( name.equals("str_new") || name.equals("num_new"))) {
					add("Class variables and instance variables may only be initialized "
							+ "with literal expressions: " + nv.getName()
							+ " '" + name + "'");
				}
			}
		}
		
	}
	
	public void inAClass(AClass node) {
		curClass = node;
		methods.put(node, new ArrayList<String>());
	}
	
	// Checks if a method has a valid main-method-signature
	private boolean isMain(AMethod node) {
		String methName = node.getName().toString().trim();
		return (methName.equals("main") && node.getModifier() instanceof APublStatModifier
				&& node.getType() instanceof ATypeType
				&& ((ATypeType)node.getType()).getName().toString().trim().equals("num")
				&& node.getParams() != null && node.getParams().size() == 1
				&& node.getParams().get(0) instanceof ANamedVariable
				&& ((ANamedVariable)node.getParams().get(0)).getType() instanceof ATypeType
				&& ((ATypeType)(((ANamedVariable)node.getParams().get(0)).getType())).getArray()
					instanceof ATrueFlag
				&& ((ATypeType)(((ANamedVariable)node.getParams().get(0)).getType())).
					getName().toString().trim().equals("str"));
	}
	
	@Override
	public void inAMethod(AMethod node) {
		String className = curClass.getName().get(curClass.getName().size() - 1).toString().trim();
		String methName = node.getName().toString().trim();
		
		if (mainFound == true) {
			if (isMain(node)) {
				add("More than one main-Method found in source file");
			} 
		} else {
			if (isMain(node)) {
				mainFound = true;
			}
		}
		
		List<String> cmeths = methods.get(curClass);
		if (cmeths.contains(node.getName().toString().trim())) {
			add("Method overloading not supported (" + methName + " in class " + className + ")");
		}
		cmeths.add(node.getName().toString().trim());
		
		Set<String> paramNames = new HashSet<String>();
		for (PVariable var : node.getParams()) {
			if (var instanceof AUnnamedVariable) {
				add("Method parameter unnamed in method " + methName + " in class " + className);
			}
			if (var instanceof ANamedVariable) {
				ANamedVariable nv = (ANamedVariable)var;
				if (paramNames.contains(nv.getName().toString().trim())) {
					add("Duplicate method parameter in method " + methName + " in class "
							+ className);
				}
				paramNames.add(nv.getName().toString().trim());
			}
		}
	}
	
	@Override
    public void inAAssignmentStatement(AAssignmentStatement node) {
		PExp id = node.getId();
		if (!(id instanceof ADotExp) && !(id instanceof ATupleExp)
				&& !(id instanceof AMethodcallExp)) {
			add("Illegal expression on left side of assignment");
		}
	}
	
	public String getContent() {
		return this.messages.toString();
	}
	
	@Override
	public void outANaturel(ANaturel node) {
		if (mainFound == false) {
			add("No valid main method found");
		}
	}
}
