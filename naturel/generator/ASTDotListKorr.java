package naturel.generator;

import java.util.LinkedList;

import naturel.generator.SammleKlassenMethoden.MethodenDef;
import naturel.generator.definitions.VariableDef;
import naturel.helpers.Helper;
import naturel.node.AAssignmentStatement;
import naturel.node.ABlock;
import naturel.node.ADeclarationStatement;
import naturel.node.ADotExp;
import naturel.node.AExpStatement;
import naturel.node.AFalseFlag;
import naturel.node.AListExp;
import naturel.node.AMethodcallExp;
import naturel.node.AMethodcalldefExp;
import naturel.node.ANamedVariable;
import naturel.node.ATypeType;
import naturel.node.Node;
import naturel.node.PExp;
import naturel.node.PStatement;
import naturel.node.TIdentifier;

public class ASTDotListKorr extends DepthVarManager {
	private int tmpVarNum = 0;
	
	private int getNextNum() {
		tmpVarNum += 1;
		return tmpVarNum;
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
    
    private String getClass(PExp e, String remClass) {
		AMethodcallExp mcall = null;
		if (e instanceof AMethodcallExp) {
			mcall = (AMethodcallExp) e;
		} else if (e instanceof AMethodcalldefExp) {
			mcall = (AMethodcallExp) ((AMethodcalldefExp) e).getMethod();
			if (remClass != null) {
				throw new RuntimeException("Error in Program: Collision of Classes: " + remClass + " - " + 
						((AMethodcalldefExp) e).getClazz().toString().trim());
			}
			remClass = ((AMethodcalldefExp) e).getClazz().toString().trim();
		} else {
			throw new RuntimeException("Error in Program: node instanceof " + e.getClass().getSimpleName());
		}
    	
    	if ((mcall.getArgs() == null) || (mcall.getArgs().size() <= 0)) {
    		// Variable
    		VariableDef vdef = searchVariable(mcall.getId().toString().trim(), remClass);
    		if (vdef == null) {
    			throw new RuntimeException("Error in Programm: VariableDef not found: Name: " + 
    					mcall.getId().toString().trim() + " at [" + mcall.getId().getLine() + "," + 
    					mcall.getId().getPos() + "]; Class: " + remClass);
    		}
    		//rememberClass = vdef.className;
    		return vdef.typ;
    	} else {
    		// Methode
    		MethodenDef mdef = searchMethode(mcall.getId().toString().trim(), remClass);
    		if (mdef == null) {
    			throw new RuntimeException("Error in Programm: MethodenDef not found: Name: " + 
    					mcall.getId().toString().trim() + " Class: " + remClass);
    		}
    		//rememberClass = mdef.
    		return mdef.rettype;
    	}
    }
    
	@Override
    public void outAListExp(AListExp node) {
    	PExp vorher = null;
    	@SuppressWarnings("unchecked")
    	LinkedList<PExp> lst = (LinkedList<PExp>) node.getList().clone();
    	String remClass = null;
		//System.out.println("..");
    	boolean isinassign = false;
    	// Bei Zuweisungen darft das linke nur bis auf 2 aufgelöst werden.
    	if (node.parent() instanceof AAssignmentStatement) {
    		AAssignmentStatement ass = (AAssignmentStatement) node.parent();
    		isinassign = (ass.getId() == node);
    	}
    	int cnt = lst.size();
    	for(PExp e : lst) {
    		cnt--;
    		if (isinassign && (cnt == 1)) {
    			// Nichts
    		} else 	if (vorher == null) {

    			// Wenn das eine Methode ist, dann muss die zuerst ausgeführt werden, dann kann man die Variable weiterverwenden
    			boolean istmethode = false;
    			if (e instanceof AMethodcallExp) {
    				AMethodcallExp mcall = (AMethodcallExp) e;
    				if ((mcall.getArgs() == null) || (mcall.getArgs().size() > 0)) {
    					// Das ist eine Methode. Diese Ausführen
    					istmethode = true;
    				}
    			}
    			if (e instanceof AMethodcalldefExp) {
    				AMethodcallExp mcall = (AMethodcallExp) ((AMethodcalldefExp)e).getMethod();
    				if ((mcall.getArgs() == null) || (mcall.getArgs().size() > 0)) {
    					// Das ist eine Methode. Diese Ausführen
    					istmethode = true;
    				}
    			}
    			if (istmethode) {
					TIdentifier id = new TIdentifier("!ListCorrFirst_" + getNextNum(), -1, -1);
	    	        
	    	    	ATypeType typ = new ATypeType();
	    	    	// Den Typ rausfinden.
	    	    	typ.setName(new TIdentifier(getClass(e, remClass), -1, -1));
	    	    	typ.setArray(new AFalseFlag());
	    	    	
	    	    	ANamedVariable namedVar = new ANamedVariable();
	    	    	namedVar.setName(id);
	    	    	namedVar.setType(typ);
	    	    	namedVar.setVal(Helper.clonePExp(e));
	    	    	
	    	    	ADeclarationStatement dec = new ADeclarationStatement();
	    	    	dec.setVar(namedVar);
	    	    	
	    	    	((Node)dec).parent(methodblock);
	    	    	// Wohin?
	    	    	methodlist.add(methodindex, dec);
	    	    	methodindex = methodindex + 1;
	    	    	
	    	    	vorher = new AMethodcallExp((TIdentifier) id.clone(), new LinkedList<PExp>());
	    	    	
	    	    	// Klasse merken
	    			remClass = getClass(e, null);
    			} else {
        			vorher = e;
        			// Klasse merken
        			remClass = getClass(e, remClass);
    			}
    		} else if ((node.parent() instanceof AExpStatement) && (lst.getLast() == e)) {
    			// Wenn es ein Methodenaufruf ist (keine Zuweisung), dann das letzre ausführen,
    			// und nicht einer Variable zuweisen.
    			ADotExp dot = new ADotExp(vorher, e);
    			
    			AExpStatement exps = new AExpStatement(dot);
    			
    	    	((Node)exps).parent(methodblock);
    	    	// Wohin?
    	    	methodlist.add(methodindex, exps);
    	    	methodindex = methodindex + 1;
    		} else {
    			ADotExp dot = new ADotExp(vorher, e);
    			
    			// Anlegen, WAS eingefügt werden soll
    	        TIdentifier id = new TIdentifier("!ListCorr_" + getNextNum(), -1, -1);
    	        
    	    	ATypeType typ = new ATypeType();
    	    	// Den Typ rausfinden.
    	    	typ.setName(new TIdentifier(getClass(e, remClass), -1, -1));
    	    	typ.setArray(new AFalseFlag());

    	    	remClass = getClass(e, remClass);

    	    	ANamedVariable namedVar = new ANamedVariable();
    	    	namedVar.setName(id);
    	    	namedVar.setType(typ);
    	    	namedVar.setVal(Helper.clonePExp((PExp)dot));
    	    	
    	    	ADeclarationStatement dec = new ADeclarationStatement();
    	    	dec.setVar(namedVar);
    	    	
    	    	((Node)dec).parent(methodblock);
    	    	// Wohin?
    	    	methodlist.add(methodindex, dec);
    	    	methodindex = methodindex + 1;
    	    	
    	    	vorher = new AMethodcallExp((TIdentifier) id.clone(), new LinkedList<PExp>());
    	    	
    		}
    	}
    	if (node.parent() instanceof AExpStatement) {
    		// War ein Methodenaufruf? Dann die Liste wegschmeissen
    		ABlock bl = (ABlock) ((AExpStatement)node.parent()).parent();
    		bl.getStatements().remove(node.parent());
    	} else if (vorher != null) {
    		// Jetzt die Liste mit dem letzen Variable ersetzen
    		node.replaceBy(vorher);
    	}

    }
	
}