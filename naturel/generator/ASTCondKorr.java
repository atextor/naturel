package naturel.generator;

import java.util.LinkedList;

import naturel.helpers.Helper;
import naturel.node.AAssignmentStatement;
import naturel.node.ABlock;
import naturel.node.ADeclarationStatement;
import naturel.node.AExpStatement;
import naturel.node.AFalseFlag;
import naturel.node.AFnumExp;
import naturel.node.AIdentifierExp;
import naturel.node.AIfStatement;
import naturel.node.AMethodcallExp;
import naturel.node.ANamedVariable;
import naturel.node.ANoneExp;
import naturel.node.ANumExp;
import naturel.node.AStringExp;
import naturel.node.ATypeType;
import naturel.node.AWhileStatement;
import naturel.node.Node;
import naturel.node.PExp;
import naturel.node.PStatement;
import naturel.node.TIdentifier;

public class ASTCondKorr extends DepthVarManager {
	private int tmpVarNum = 0;
	
	private int getNextNum() {
		tmpVarNum += 1;
		return tmpVarNum;
	}
	
    public AMethodcallExp handle(ABlock bl, Node akt, PExp cond, ABlock whileBlock) {
        // Egal, ob da nur ein Wert ist, den man schon verwenden könnte, oder ein
    	// zusammengesetzer Ausdruck... Auslagern. Optimieren ist nicht die Aufgabe...
        
        // Wo soll das hin?
		LinkedList<PStatement> list = bl.getStatements();
		int index = list.indexOf(akt);
		if (index < 0) {
			throw new RuntimeException("Processing Error");
		}
        
        // Anlegen, WAS eingefügt werden soll
        TIdentifier id = new TIdentifier("!condCorr_" + getNextNum(), -1, -1);
        
    	ATypeType typ = new ATypeType();
    	typ.setName(new TIdentifier("bool", -1, -1));
    	typ.setArray(new AFalseFlag());
    	
    	ANamedVariable namedVar = new ANamedVariable();
    	namedVar.setName(id);
    	namedVar.setType(typ);
    	//namedVar.setVal((PExp) cond.clone());
    	namedVar.setVal(Helper.clonePExp((PExp)cond));
    	
    	ADeclarationStatement dec = new ADeclarationStatement();
    	dec.setVar(namedVar);
    	
    	((Node)dec).parent(bl);
    	
    	// Und einfügen
    	list.add(index, dec);
    	
    	// Wenn das ein while ist, so muss die Bedingung am Ende des Blockes erneut berechnet werden
    	if (whileBlock != null) {
    		AAssignmentStatement assign = new AAssignmentStatement();
    		AIdentifierExp identexp = new AIdentifierExp((TIdentifier)id.clone());
    		assign.setId(identexp);
    		//assign.setVal((PExp)cond.clone());
    		assign.setVal(Helper.clonePExp(cond));
    		((Node)assign).parent(whileBlock);
    		whileBlock.getStatements().add(assign);
    	}
    	
    	// Jetzt die Condition ersetzen
    	AMethodcallExp var = new AMethodcallExp();
    	var.setId((TIdentifier) id.clone());
    	
    	return var;
    }

    public void inAWhileStatement(AWhileStatement node) {
        defaultIn(node);
        ABlock bl = (ABlock)node.parent();
    	node.setCond(handle(bl, node, node.getCond(), (ABlock) node.getBlock()));
    }
	
    public void inAIfStatement(AIfStatement node) {
        defaultIn(node);
        ABlock bl = (ABlock)node.parent();
    	node.setCond(handle(bl, node, node.getCond(), null));
    }
    
    ABlock methodblock = null;
    LinkedList<PStatement> methodlist = null;
    int methodindex = -1;
    
    /**
     * Um die Methodenparameter flachzuklopfen wird die Zeile benötigt, die vor der aktuellen Zeilen ist.
     * Daher hier die Zeile merken. Wenn das dann wirklich ein AMethodcallExp ist, dann hat es die Werte
     */
    public void inAXStatement(Node node) {
    	methodblock = (ABlock)node.parent();
    	methodlist = methodblock.getStatements();
		methodindex = methodlist.indexOf(node);
		if (methodindex < 0) {
			throw new RuntimeException("Processing Error (inAXStatement)");
		}
    }
    
    public void outAXStatement(Node node) {
    	methodblock = null;
    	methodlist = null;
    	methodindex = -1;
    }

    public void inAExpStatement(AExpStatement node) {
    	inAXStatement(node);
    }

    public void outAExpStatement(AExpStatement node) {
    	outAXStatement(node);
    }
    
    public void inADeclarationStatement(ADeclarationStatement node) {
    	super.inADeclarationStatement(node);
    	inAXStatement(node);
    }
    
    public void outADeclarationStatement(ADeclarationStatement node) {
    	outAXStatement(node);
    }
    
    public void inAAssignmentStatement(AAssignmentStatement node) {
    	inAXStatement(node);
    }
    
    public void outAAssignmentStatement(AAssignmentStatement node) {
    	outAXStatement(node);
    }
      
    public void inAMethodcallExp(AMethodcallExp node) {
    	if (methodindex >= 0) {
        	if ((node.getArgs() != null) && (node.getArgs().size() > 0)) {
        		// Keine Variable
        		for (int i = 0; i < node.getArgs().size(); i++) {
        			PExp ex = node.getArgs().get(i);
       				if (( ! (ex instanceof ANoneExp)) && 
           				( ! (ex instanceof ANumExp)) &&
           				( ! (ex instanceof AFnumExp)) &&
           				( ! (ex instanceof AStringExp))) {
       					// Alles rausziehen, auch wenn es nur eine Variable ist.
       					// Auf Performace optimieren wir nicht
       					
       			        // Anlegen, WAS eingefügt werden soll
       			        TIdentifier id = new TIdentifier("!paramCorr_" + getNextNum(), -1, -1);
       			        
       			    	ATypeType typ = new ATypeType();
       			    	typ.setName(new TIdentifier(getType(ex), -1, -1));
       			    	typ.setArray(new AFalseFlag());
       			    	   			    	
       			    	ANamedVariable namedVar = new ANamedVariable();
       			    	namedVar.setName(id);
       			    	namedVar.setType(typ);
       			    	namedVar.setVal((PExp) ex);
       			    	
       			    	ADeclarationStatement dec = new ADeclarationStatement();
       			    	dec.setVar(namedVar);
       			    	
       			    	((Node)dec).parent(methodblock);
       			    	
       			    	// Und einfügen
       			    	methodlist.add(methodindex, dec);
       			    	methodindex++;
       			    	
       			    	// Parameter umleiten
       			    	AMethodcallExp param = new AMethodcallExp((TIdentifier) id.clone(), new LinkedList<PExp>());
       			    	param.parent(node);
       			    	//node.getArgs().remove(i); <- nicht notwendig, da die Zeile "namedVar.setVal((PExp) ex);" das schon aus der Lsite nimmt
       			    	node.getArgs().add(i, param);
       				}
        		}
   			}
    	}
    }
    
}
