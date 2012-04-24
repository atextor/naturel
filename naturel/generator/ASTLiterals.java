package naturel.generator;

import java.util.ArrayList;
import java.util.List;

import naturel.analysis.DepthFirstAdapter;
import naturel.node.AMethodcallExp;
import naturel.node.ANumExp;
import naturel.node.AStringExp;
import naturel.node.Node;
import naturel.node.PExp;
import naturel.node.TIdentifier;

/**
 * Ersetzt Zahl- und String-Literale durch entsprechende Objekte.
 * @author Andreas Textor
 *
 * @since Jan 22, 2008
 * @version Jan 22, 2008
 */
public class ASTLiterals extends DepthFirstAdapter {
	/**
	 * Erzeugt aus einer literalen Expression den passenden Konstruktoraufruf
	 */
	private AMethodcallExp makeNewConstr(PExp node, String type) {
		List<PExp> param = new ArrayList<PExp>();
		param.add((PExp)(node.clone()));
		AMethodcallExp meth = new AMethodcallExp(new TIdentifier(type + "_new"), param);
		return meth;
	}
	
	@Override
	public void defaultIn(Node node) {
		if (node instanceof ANumExp) {
			AMethodcallExp meth = makeNewConstr((PExp)node, "num");
			node.replaceBy(meth);
		}
		if (node instanceof AStringExp) {
			AMethodcallExp meth = makeNewConstr((PExp)node, "str");
			node.replaceBy(meth);
		}
	}
	

	/*
	@Override
	public void caseAUnnamedVariable(AUnnamedVariable node) {
		inAUnnamedVariable(node);
		if (node.getType() != null) {
			node.getType().apply(this);
		}
		if (node.getVal() != null) {
			node.getVal().apply(this);
		}
		outAUnnamedVariable(node);
	}

	@Override
	public void caseANamedVariable(ANamedVariable node) {
		inANamedVariable(node);
		if (node.getName() != null) {
			node.getName().apply(this);
		}
		if (node.getType() != null) {
			node.getType().apply(this);
		}
		if (node.getVal() != null) {
			node.getVal().apply(this);
		}
		outANamedVariable(node);
	}

	@Override
	public void caseAIfStatement(AIfStatement node) {
		inAIfStatement(node);
		if (node.getCond() != null) {
			node.getCond().apply(this);
		}
		if (node.getBlock() != null) {
			node.getBlock().apply(this);
		}
		outAIfStatement(node);
	}

	@Override
	public void caseAWhileStatement(AWhileStatement node) {
		inAWhileStatement(node);
		if (node.getCond() != null) {
			node.getCond().apply(this);
		}
		if (node.getBlock() != null) {
			node.getBlock().apply(this);
		}
		outAWhileStatement(node);
	}

	@Override
	public void caseAAssignmentStatement(AAssignmentStatement node) {
		inAAssignmentStatement(node);
		if (node.getId() != null) {
			node.getId().apply(this);
		}
		if (node.getVal() != null) {
			node.getVal().apply(this);
		}
		outAAssignmentStatement(node);
	}

	@Override
	public void caseADeclarationStatement(ADeclarationStatement node) {
		inADeclarationStatement(node);
		if (node.getVar() != null) {
			node.getVar().apply(this);
		}
		outADeclarationStatement(node);
	}

	@Override
	public void caseAExpStatement(AExpStatement node) {
		inAExpStatement(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
		}
		outAExpStatement(node);
	}

	@Override
	public void caseANumExp(ANumExp node) {
		inANumExp(node);
		if (node.getNum() != null) {
			node.getNum().apply(this);
		}
		outANumExp(node);
	}

	@Override
	public void caseAFnumExp(AFnumExp node) {
		inAFnumExp(node);
		if (node.getFnum() != null) {
			node.getFnum().apply(this);
		}
		outAFnumExp(node);
	}

	@Override
	public void caseAStringExp(AStringExp node) {
		inAStringExp(node);
		if (node.getStr() != null) {
			node.getStr().apply(this);
		}
		outAStringExp(node);
	}

	@Override
	public void caseATupleExp(ATupleExp node) {
		inATupleExp(node);
		{
			List<PExp> copy = new ArrayList<PExp>(node.getValues());
			for (PExp e : copy) {
				e.apply(this);
			}
		}
		outATupleExp(node);
	}
	*/
}
