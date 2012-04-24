package naturel;

import naturel.analysis.DepthFirstAdapter;
import naturel.node.AFnumExp;
import naturel.node.AMethod;
import naturel.node.AMethodcallExp;
import naturel.node.ANamedVariable;
import naturel.node.ANumExp;
import naturel.node.AStringExp;
import naturel.node.ATypeType;
import naturel.node.Node;
import naturel.node.Start;
import naturel.node.TIdentifier;

/**
 * ASTPrinter, der maximal möglich generisch ist
 * @author rre
 */
public class ASTPrinter2 extends DepthFirstAdapter {
	private String ausgabe = "";
	private int einrueckung = 0;
	
	private void schreibeID(TIdentifier id) {
		ausgabe = ausgabe + ": " + id.toString().trim() + " [" + id.getLine() + "," + id.getPos() + "]";
	}
	
	@Override
    public void defaultIn(@SuppressWarnings("unused") Node node) {
		for (int x = 0; x < einrueckung; x++) {
			ausgabe = ausgabe + "\t";
		}
		ausgabe = ausgabe + node.getClass().getSimpleName();
		// Jetzt spezifische Ausgabe
		if (node instanceof AMethod) {
			AMethod meth = (AMethod) node;
			//ausgabe = ausgabe + ": " + meth.getName().toString();
			schreibeID(meth.getName());
		}
		if (node instanceof ANamedVariable) {
			ANamedVariable namedVar = (ANamedVariable) node;
			//ausgabe = ausgabe + ": " + namedVar.getName().toString();
			schreibeID(namedVar.getName());
		}
		if (node instanceof AMethodcallExp) {
			AMethodcallExp methCall = (AMethodcallExp) node;
			//ausgabe = ausgabe + ": " + methCall.getId().toString();
			schreibeID(methCall.getId());
			ausgabe = ausgabe + " (" + methCall.getArgs().size() + ")";
		}
		if (node instanceof ATypeType) {
			ATypeType typtyp = (ATypeType) node;
			schreibeID(typtyp.getName());
		}
		if (node instanceof ANumExp) {
			ANumExp num = (ANumExp) node;
			ausgabe = ausgabe + ": " + num.getNum().toString().trim() + " [" + num.getNum().getLine() + "," + num.getNum().getPos() + "]";
		}
		if (node instanceof AFnumExp) {
			AFnumExp fnum = (AFnumExp) node;
			ausgabe = ausgabe + ": " + fnum.getFnum().toString().trim() + " [" + fnum.getFnum().getLine() + "," + fnum.getFnum().getPos() + "]";
		}
		if (node instanceof AStringExp) {
			AStringExp str = (AStringExp) node;
			ausgabe = ausgabe + ": " + str.getStr().toString().trim() + " [" + str.getStr().getLine() + "," + str.getStr().getPos() + "]";
		}
		
		ausgabe = ausgabe + "\n";
		einrueckung += 1;
	}

	@Override
    public void defaultOut(@SuppressWarnings("unused") Node node) {
		einrueckung -= 1;
    }
	
	public String toString() {
		return ausgabe;
	}
	
	@Override
    public void inStart(Start node) {
		ausgabe = "";
        defaultIn(node);
    }
}
