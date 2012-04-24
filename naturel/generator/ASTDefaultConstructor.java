package naturel.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import naturel.analysis.DepthFirstAdapter;
import naturel.node.AAssignmentStatement;
import naturel.node.ABlock;
import naturel.node.AClass;
import naturel.node.ADeclaration;
import naturel.node.ADefaultType;
import naturel.node.AMethod;
import naturel.node.AMethodcallExp;
import naturel.node.ANamedVariable;
import naturel.node.ANoneExp;
import naturel.node.APrivStatModifier;
import naturel.node.AProtStatModifier;
import naturel.node.APublStatModifier;
import naturel.node.PDeclaration;
import naturel.node.PExp;
import naturel.node.PModifier;
import naturel.node.PVariable;
import naturel.node.TIdentifier;

/**
 * Generiert für Klassen ohne Konstruktor eine leere Default-Konstruktor-Methode.
 * Ausserdem wird für Klassen ohne explizite Oberklasse "Object" angegeben.
 * Initialisierungen von Exemplarvariablen werden in den Konstruktor verschoben.
 *
 * @author Andreas Textor
 * @since Jan 21, 2008
 * @version Jan 21, 2008
 */
public class ASTDefaultConstructor extends DepthFirstAdapter {
	AClass curClass = null;

	Map<AClass, AMethod> constr = new HashMap<AClass, AMethod>();

	public void inAClass(AClass node) {
		curClass = node;
		if (node.getSuper() == null || node.getSuper().size() < 1) {
			List<TIdentifier> supernode = new ArrayList<TIdentifier>();
			supernode.add(new TIdentifier("Object"));
			node.setSuper(supernode);
		}
	}

	public void inAMethod(AMethod node) {
		if (node.getName().toString().trim().equals("new")) {
			constr.put(curClass, node);
		}
	}
	
	public void outAClass(AClass node) {
		AMethod constructor;
		
		// Es gibt keinen Konstruktor. Legen wir einen Default-Konstruktor an.
		if (constr.get(node) == null) {
			AMethod meth = new AMethod(new APublStatModifier(),
			        new TIdentifier("new"), new ADefaultType(),
			        new LinkedList<PVariable>(), new ABlock());
			meth.parent(node);
			node.getMethods().add(0, meth);
			constructor = meth;
		} else {
			constructor = constr.get(node);
		}
		
		// Initialisierungen von Exemplarvariablen in den Konstruktor verschieben
		for (PDeclaration decl : node.getVars()) {
			ADeclaration d = (ADeclaration)decl;
			PModifier modifier = d.getModifier();
			// Uns interessieren keine Klassenvariablen
			if (modifier instanceof APublStatModifier || modifier instanceof APrivStatModifier
					|| modifier instanceof AProtStatModifier) {
				continue;
			}
			ANamedVariable var = (ANamedVariable)d.getVar();
			TIdentifier name = (TIdentifier)var.getName().clone();
			name.setLine(-2);
			PExp value = (PExp)var.getVal().clone();
			var.setVal(new ANoneExp());
			
			ABlock body = (ABlock)constructor.getBody();
			AAssignmentStatement asgn = new AAssignmentStatement(
					new AMethodcallExp(name, new LinkedList<PExp>()), value);
			asgn.parent(body);
			body.getStatements().add(0, asgn);
		}
	}

}
