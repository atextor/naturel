package naturel.generator;

import java.util.ArrayList;
import java.util.List;

import naturel.analysis.DepthFirstAdapter;
import naturel.node.ABlock;
import naturel.node.ADeclarationStatement;
import naturel.node.AMethod;
import naturel.node.PStatement;

/**
 * Die Klasse sammelt alle Variablen-Deklarationen aus Methoden auf und steckt sie an den Anfang
 * der jeweiligen Methode.
 * 
 * @author Andreas Textor
 *
 * @since Jan 14, 2008
 * @version Jan 14, 2008
 */
public class ASTVarCollect extends DepthFirstAdapter {
	List<ADeclarationStatement> declarations = new ArrayList<ADeclarationStatement>();

	@Override
	public void caseABlock(ABlock node) {
		List<ADeclarationStatement> decls = new ArrayList<ADeclarationStatement>();
		for (PStatement e : node.getStatements()) {
			if (e instanceof ADeclarationStatement) {
				decls.add((ADeclarationStatement)e);
			} else {
				e.apply(this);
			}
		}
		node.getStatements().removeAll(decls);
		declarations.addAll(decls);
	}

	public void outAMethod(AMethod node) {
		defaultOut(node);
		ABlock methodBlock = (ABlock)node.getBody();
		
		for (PStatement s : declarations) {
			methodBlock.getStatements().add(0, s);
			s.parent(null);
		}
		declarations.clear();
	}
}
