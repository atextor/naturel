package naturel.generator;

import java.util.ArrayList;
import java.util.List;

import naturel.node.ADivExp;
import naturel.node.ADotExp;
import naturel.node.AEqExp;
import naturel.node.AGtExp;
import naturel.node.AGteqExp;
import naturel.node.ALtExp;
import naturel.node.ALteqExp;
import naturel.node.AMethodcallExp;
import naturel.node.AMinusExp;
import naturel.node.AModExp;
import naturel.node.AMultExp;
import naturel.node.ANeqExp;
import naturel.node.APlusExp;
import naturel.node.Node;
import naturel.node.PExp;
import naturel.node.TIdentifier;

/**
 *  Verwandelt x+y in x.add(y) usw. 
 *
 * @author Andreas Textor
 * @since Jan 23, 2008
 * @version Jan 23, 2008
 */
public class ASTOperators extends DepthVarManager {
	/**
	 * Macht aus num_new(2) + num_new(3) ein num_new(2).add(num_new(3)) 
	 */
	private ADotExp makeMethodCall(ILRNode node, String method) {
		List<PExp> param = new ArrayList<PExp>();
		param.add((PExp)node.getR().clone());
		PExp l = (PExp)node.getL().clone();
		PExp r = new AMethodcallExp(new TIdentifier(method), param);
		return new ADotExp(l, r);
	}
	
	@Override
	public void defaultOut(Node node) {
		if (node instanceof APlusExp) {
			// Plus kann entweder Addition von 2 num sein oder String-Konkatenation
			APlusExp plus = (APlusExp)node;
			if (getType(plus.getL()).equals("str")) {
				node.replaceBy(makeMethodCall((ILRNode)node, "append"));
			} else {
				node.replaceBy(makeMethodCall((ILRNode)node, "addNum"));
			}
		}
		if (node instanceof AMinusExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "subNum"));
		}
		if (node instanceof AMultExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "multNum"));
		}
		if (node instanceof ADivExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "divNum"));
		}
		if (node instanceof AModExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "modNum"));
		}
		if (node instanceof AEqExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "eq"));
		}
		if (node instanceof ANeqExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "neq"));
		}
		if (node instanceof ALteqExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "lteq"));
		}
		if (node instanceof ALtExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "lt"));
		}
		if (node instanceof AGteqExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "gteq"));
		}
		if (node instanceof AGtExp) {
			node.replaceBy(makeMethodCall((ILRNode)node, "gt"));
		}
	}
}
