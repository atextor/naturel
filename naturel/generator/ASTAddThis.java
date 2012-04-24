package naturel.generator;

import java.util.ArrayList;
import java.util.List;

import naturel.generator.SammleKlassenMethoden.ClassDef;
import naturel.generator.SammleKlassenMethoden.MethodenDef;
import naturel.generator.definitions.VariableDef;
import naturel.helpers.Helper;
import naturel.node.AListExp;
import naturel.node.AMethodcallExp;
import naturel.node.AMethodcalldefExp;
import naturel.node.PExp;
import naturel.node.TIdentifier;

/**
 * Bei Aufrufen, die Klassen-Variablen oder Klassenmethoden betreffen,
 * bei denen kein this davor steht, wird das hier davor generiert
 * @author rre
 *
 */
public class ASTAddThis extends DepthVarManager {
	
	private boolean genthis(AMethodcallExp node) {
		// Wenn der erste Node etwas aus der Klasse ist, dann ein this davor kloppen
   		String name = node.getId().toString().trim();
    	boolean addthis = false;
		if ((node.getArgs() != null) && (node.getArgs().size() > 0)) {
			// Methode
			MethodenDef mdef = searchMethode(name, null);
			if (mdef == null) {
				throw new RuntimeException("Error in Program: Method not found: " + name + 
						" at [" + node.getId().getLine() + "," + node.getId().getPos() + "]");
			}
			if ((mdef.className != null) && (!mdef.statisch)) {
				ClassDef cdef = alleKlassenMethoden.get(mdef.className);
				//TODO auf null abfragen
				while (cdef.superof != null) {
					// Könnte ja aus der Vererbung kommen. Daher den Baum durchgehen
					if (mdef.className.equals(cdef.name)) {
						addthis = true;
					}
					cdef = alleKlassenMethoden.get(cdef.superof);
				}
			}
		} else {
			// Variable
			VariableDef vdef = searchVariable(name, null);
			if (vdef == null) {
				throw new RuntimeException("Error in Program: Variable not found: " + name + 
						" at [" + node.getId().getLine() + "," + node.getId().getPos() + "]");
			}
			if ((vdef.className != null) && (!vdef.statisch)) {
				ClassDef cdef = alleKlassenMethoden.get(vdef.className);
				//TODO auf null abfragen
				while (cdef.superof != null) {
					// Könnte ja aus der Vererbung kommen. Daher den Baum durchgehen
					if (vdef.className.equals(cdef.name)) {
						addthis = true;
					}
					cdef = alleKlassenMethoden.get(cdef.superof);
				}
			}
		}
		return addthis;
	}
	
	@Override
    public void outAListExp(AListExp node) {
		// Wenn der erste Node etwas aus der Klasse ist, dann ein this davor kloppen
    	PExp e = node.getList().get(0);
    	if (e instanceof AMethodcallExp) {
    		// AMethodCallDefExp interessiert nicht, da da eine Klasse drinsteht.
    		if (genthis((AMethodcallExp) e)) {
    			AMethodcallExp neu = new AMethodcallExp(new TIdentifier("this", 0, 0), new ArrayList<PExp>());
    			node.getList().add(0, neu);
    		}
    	}
	}

	@Override
	public void outAMethodcallExp(AMethodcallExp node) {
		// Wenn der erste Node etwas aus der Klasse ist, dann ein this davor kloppen
		if (!(node.parent() instanceof AListExp) &&
				!(node.parent() instanceof AMethodcalldefExp)) {
			// Listen werden oben abgearbeitet, AMethodcalldefExp sind auch nicht interessant
			if (genthis(node)) {
				// Daraus eine DOT-Liste machen

				//Aktuellen Node clonen, da wir den anderen vermurksen
				PExp merk = Helper.clonePExp(node);
				
				// List-Node
				AListExp lstnode = new AListExp();
				node.replaceBy(lstnode);
				
				List<PExp> lst = new ArrayList<PExp>();

				// this anlegen
				AMethodcallExp neu = new AMethodcallExp(new TIdentifier("this", 0, 0), new ArrayList<PExp>());
				
				lst.add(neu);
				lst.add(merk);
				
				lstnode.setList(lst);
			}
		}
	}
}
