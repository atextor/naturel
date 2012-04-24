package naturel.generator;

import java.util.LinkedList;

import naturel.analysis.DepthFirstAdapter;
import naturel.node.AAssignmentStatement;
import naturel.node.ABlock;
import naturel.node.ADeclarationStatement;
import naturel.node.AMethodcallExp;
import naturel.node.ANamedVariable;
import naturel.node.Node;
import naturel.node.PExp;
import naturel.node.PStatement;
import naturel.node.TIdentifier;

/**
 * Hier werden die Initialisierungen "flachgeklopft":
 * Aus:
 *   num:x = 1;
 * wird:
 *   num:x;
 *   x = 1;
 * @author rre
 *
 */

public class ASTDecKorr extends DepthFirstAdapter {
	@Override
	public void inADeclarationStatement(ADeclarationStatement node) {
		if ((node.getVar() != null) && (node.getVar() instanceof ANamedVariable)) {
			ANamedVariable namedVar = (ANamedVariable)node.getVar();
			if (namedVar.getVal() != null) {
				// Initialisierte Variable.
				// Das zu einer Deklaration und anschließender Initialisierung flachklopfen.
				if (node.parent() instanceof ABlock) {
					ABlock bl = (ABlock)node.parent();
					LinkedList<PStatement> list = bl.getStatements();
					int index = list.indexOf(node);
					if (index < 0) {
						throw new RuntimeException("Error in expression handling");
					}
					AAssignmentStatement assign = new AAssignmentStatement();
					AMethodcallExp methexp = new AMethodcallExp((TIdentifier)namedVar.getName().clone(),
							new LinkedList<PExp>());
					
					assign.setId(methexp);
					assign.setVal((PExp)namedVar.getVal().clone());
					//assign.setVal(Helper.clonePExp(namedVar.getVal()));
					((Node)assign).parent(bl);
					list.add(index + 1, assign);
					namedVar.setVal(null);
				}
			}
		}
    }
		
	
}
