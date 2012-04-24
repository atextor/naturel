package naturel.generator;

import java.util.LinkedList;
import java.util.List;

import naturel.generator.definitions.VariableDef;
import naturel.node.ADotExp;
import naturel.node.AListExp;
import naturel.node.AMethodcallExp;
import naturel.node.AMethodcalldefExp;
import naturel.node.PExp;
import naturel.node.TIdentifier;

/**
 * Überarbeitet die ADotExpr, klopft sie in ein Array flach.
 * @author rerdt
 */
public class ASTDotExpr extends DepthVarManager {
	
    public void outADotExp(ADotExp node) {
    	AListExp list = new AListExp();
    	node.replaceBy(list);
    	if (node.getL() instanceof AListExp) {
    		AListExp links = (AListExp) node.getL();
    		list.getList().addAll(links.getList());
    	} else {
    		list.getList().add(node.getL());
    	}
    	if (node.getR() instanceof AListExp) {
    		AListExp rechts = (AListExp) node.getR();
    		list.getList().addAll(rechts.getList());
    	} else {
    		list.getList().add(node.getR());
    	}
    }
    
    /**
     * Aufdröseln der DOT-Listen
     */
    public void outAListExp(AListExp node) {
    	//String merkclass = null;
		// Es kann sein:
		// [package.]Klasse.statischeVariable
		// Objetk.variable
		// Objekt.mehtode()
		// Objekt.methode().variable
		// Objekt.methode().methode()
		// Objekt.methode().variable...
    	String merkClass = null;
    	List<PExp> neu = new LinkedList<PExp>();
        for (PExp ex : node.getList()) {
    		if (! (ex instanceof AMethodcallExp)) {
    			throw new RuntimeException("Internal Error");
    		}
    		AMethodcallExp meth = (AMethodcallExp) ex;
    		if (merkClass != null) {
    			// vorher wurde schon ein Klasse angegeben. Nun in dieser Klasse suchen
    			boolean gefunden = false;
    			if ((meth.getArgs() == null) || (meth.getArgs().size() <= 0)) {
					// Das ist eine Variable!
    				gefunden = searchVariable(meth.getId().toString().trim(), merkClass) != null;
    			} else {
    				gefunden = searchMethode(meth.getId().toString().trim(), merkClass) != null;
    			}
				
				if (gefunden) {
					// Diese Informationen zusammenlegen
					TIdentifier classid = new TIdentifier(merkClass);
					
					AMethodcallExp classme = new AMethodcallExp();
					classme.setId(classid);
					
					AMethodcalldefExp d2 = new AMethodcalldefExp();
					d2.setMethod((PExp) ex.clone());
					d2.setClazz(classme);
					//d2.parent(node);
					
					neu.add(d2);
					merkClass = null;
				} else {
					// Dann wird das wohl eine Klasse/Paket sein.
					// TODO: Überprüfen
					merkClass = merkClass + "." + meth.getId().toString().trim();
				}
    		} else {
    			if ((meth.getArgs() == null) || (meth.getArgs().size() <= 0)) {
    				// Keine Parameter -> Varaible / Klasse
    				VariableDef vdef = searchVariable(meth.getId().toString().trim(), null);
    				if (vdef != null) {
    					//Das ist eine Variable!
    					//Das kommt ungeschnitten an die neue Liste
    					neu.add((PExp) ex.clone());
    				} else {
    					// Dann wird das wohl eine Klasse/Paket sein.
    					// TODO: Überprüfen
    					merkClass = meth.getId().toString().trim();
    				}
    			} else {
    				// Parameter -> Methode
    				// TODO: Überprüfen
    				neu.add((PExp) ex.clone());
    			}
    		}
        }
        /*System.out.println("AA   " + merkClass);
        if (neu.size() != node.getList().size()) {
        	System.out.println("AA   " + neu.size() +  "  " + node.getList().size() + " " + node);
        }*/
        node.setList(neu);
    }

}
