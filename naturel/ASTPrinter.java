package naturel;

import java.util.*;
import naturel.analysis.AnalysisAdapter;
import naturel.node.*;

public class ASTPrinter extends AnalysisAdapter {
	private String ausgabe = "";
	private int einrueckung = 0;
	private boolean rueck = true;

	private void add(Node n, String str) {
		if (rueck) {
			for (int x = 0; x < einrueckung; x++) {
				ausgabe = ausgabe + "\t";
			}
		}
		rueck = true;
		ausgabe = ausgabe + n.getClass().getSimpleName();
		if (!str.equals("")) {
			ausgabe = ausgabe + ":" + str;
		}
		ausgabe = ausgabe + "\n";
	}
	
	private void add(String str) {
		if (rueck) {
			for (int x = 0; x < einrueckung; x++) {
				ausgabe = ausgabe + "\t";
			}
		}
		ausgabe = ausgabe + str + ":";
		rueck = false;
	}
	
	private void add(Node n) {
		if (n instanceof TIdentifier) {
			add(n, ((TIdentifier)n).toString());
		} else {
			add(n, "");
		}
	}
	
	private void addn() {
		ausgabe = ausgabe + "\n";
		rueck = true;
	}

	private void incEinrueckung() {
		einrueckung++;
	}

	private void decEinrueckung() {
		einrueckung--;
	}

	public String toString() {
		return ausgabe;
	}

	public void inStart(Start node) {
		defaultIn(node);
	}

	public void outStart(Start node) {
		defaultOut(node);
	}

	public void defaultIn(@SuppressWarnings("unused")
	Node node) {
		// Do nothing
	}

	public void defaultOut(@SuppressWarnings("unused")
	Node node) {
		// Do nothing
	}

	@Override
	public void caseStart(Start node) {
		inStart(node);
		ausgabe  = ""; // Damit ist ein "Recycling" möglich
		node.getPNaturel().apply(this);
		node.getEOF().apply(this);
		outStart(node);
	}

	public void inANaturel(ANaturel node) {
		defaultIn(node);
	}

	public void outANaturel(ANaturel node) {
		defaultOut(node);
	}

	@Override
	public void caseANaturel(ANaturel node) {
		inANaturel(node);
		add(node);
		incEinrueckung();
		{
			List<PImport> copy = new ArrayList<PImport>(node.getUses());
			for (PImport e : copy) {
				add("import");
				e.apply(this);
			}
		}
		{
			List<PClass> copy = new ArrayList<PClass>(node.getClasses());
			for (PClass e : copy) {
				add("class");
				e.apply(this);
			}
		}
		decEinrueckung();
		outANaturel(node);
	}

	public void inAOneOptionalIdentifier(AOneOptionalIdentifier node) {
		defaultIn(node);
	}

	public void outAOneOptionalIdentifier(AOneOptionalIdentifier node) {
		defaultOut(node);
	}

	@Override
	public void caseAOneOptionalIdentifier(AOneOptionalIdentifier node) {
		inAOneOptionalIdentifier(node);
		add(node);
		incEinrueckung();		
		if (node.getId() != null) {
			node.getId().apply(this);
		}
		decEinrueckung();
		outAOneOptionalIdentifier(node);
	}

	public void inANoneOptionalIdentifier(ANoneOptionalIdentifier node) {
		defaultIn(node);
	}

	public void outANoneOptionalIdentifier(ANoneOptionalIdentifier node) {
		defaultOut(node);
	}

	@Override
	public void caseANoneOptionalIdentifier(ANoneOptionalIdentifier node) {
		inANoneOptionalIdentifier(node);
		add(node);
		outANoneOptionalIdentifier(node);
	}

	public void inAImport(AImport node) {
		defaultIn(node);
	}

	public void outAImport(AImport node) {
		defaultOut(node);
	}

	@Override
	public void caseAImport(AImport node) {
		inAImport(node);
		add(node);
		incEinrueckung();
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getName());
			for (TIdentifier e : copy) {
				add("name");
				e.apply(this);
				addn();
			}
		}
		if (node.getAs() != null && (node.getAs() instanceof AOneOptionalIdentifier)) {
			add("as");
			node.getAs().apply(this);
			addn();
		}
		decEinrueckung();
		outAImport(node);
	}

	public void inAClass(AClass node) {
		defaultIn(node);
	}

	public void outAClass(AClass node) {
		defaultOut(node);
	}

	@Override
	public void caseAClass(AClass node) {
		inAClass(node);
		add(node);
		incEinrueckung();
		
		if (node.getModifier() != null) {
			add("modifier");
			node.getModifier().apply(this);
			addn();
		}
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getName());
			for (TIdentifier e : copy) {
				add("name");
				e.apply(this);
				addn();
			}
		}
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getSuper());
			for (TIdentifier e : copy) {
				add("super");
				e.apply(this);
				addn();
			}
		}
		{
			List<PInterface> copy = new ArrayList<PInterface>(node.getInterfaces());
			for (PInterface e : copy) {
				add("interface");
				e.apply(this);
				addn();
			}
		}
		{
			List<PDeclaration> copy = new ArrayList<PDeclaration>(node.getVars());
			for (PDeclaration e : copy) {
				add("variable");
				e.apply(this);
				addn();
			}
		}
		
		{
			List<PMethod> copy = new ArrayList<PMethod>(node.getMethods());
			for (PMethod e : copy) {
				add("method");
				e.apply(this);
				addn();
			}
		}
		decEinrueckung();
		outAClass(node);
	}

	public void inAInterfaceInterface(AInterfaceInterface node) {
		defaultIn(node);
	}

	public void outAInterfaceInterface(AInterfaceInterface node) {
		defaultOut(node);
	}

	@Override
	public void caseAInterfaceInterface(AInterfaceInterface node) {
		inAInterfaceInterface(node);
		add(node);
		incEinrueckung();
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getName());
			for (TIdentifier e : copy) {
				add("name");
				e.apply(this);
				addn();
			}
		}
		decEinrueckung();
		outAInterfaceInterface(node);
	}

	public void inAPublModifier(APublModifier node) {
		defaultIn(node);
	}

	public void outAPublModifier(APublModifier node) {
		defaultOut(node);
	}

	@Override
	public void caseAPublModifier(APublModifier node) {
		inAPublModifier(node);
		add(node);
		outAPublModifier(node);
	}

	public void inAPrivModifier(APrivModifier node) {
		defaultIn(node);
	}

	public void outAPrivModifier(APrivModifier node) {
		defaultOut(node);
	}

	@Override
	public void caseAPrivModifier(APrivModifier node) {
		inAPrivModifier(node);
		add(node);
		outAPrivModifier(node);
	}

	public void inAProtModifier(AProtModifier node) {
		defaultIn(node);
	}

	public void outAProtModifier(AProtModifier node) {
		defaultOut(node);
	}

	@Override
	public void caseAProtModifier(AProtModifier node) {
		inAProtModifier(node);
		add(node);
		outAProtModifier(node);
	}

	public void inAPublStatModifier(APublStatModifier node) {
		defaultIn(node);
	}

	public void outAPublStatModifier(APublStatModifier node) {
		defaultOut(node);
	}

	@Override
	public void caseAPublStatModifier(APublStatModifier node) {
		inAPublStatModifier(node);
		add(node);
		outAPublStatModifier(node);
	}

	public void inAPrivStatModifier(APrivStatModifier node) {
		defaultIn(node);
	}

	public void outAPrivStatModifier(APrivStatModifier node) {
		defaultOut(node);
	}

	@Override
	public void caseAPrivStatModifier(APrivStatModifier node) {
		inAPrivStatModifier(node);
		add(node);
		outAPrivStatModifier(node);
	}

	public void inAProtStatModifier(AProtStatModifier node) {
		defaultIn(node);
	}

	public void outAProtStatModifier(AProtStatModifier node) {
		defaultOut(node);
	}

	@Override
	public void caseAProtStatModifier(AProtStatModifier node) {
		inAProtStatModifier(node);
		add(node);
		outAProtStatModifier(node);
	}

	public void inAMethod(AMethod node) {
		defaultIn(node);
	}

	public void outAMethod(AMethod node) {
		defaultOut(node);
	}

	@Override
	public void caseAMethod(AMethod node) {
		inAMethod(node);
		add(node);
		incEinrueckung();
		if (node.getModifier() != null) {
			add("modifier");
			node.getModifier().apply(this);
			addn();
		}
		if (node.getName() != null) {
			add("name");
			node.getName().apply(this);
			addn();
		}
		{
			List<PVariable> copy = new ArrayList<PVariable>(node.getParams());
			for (PVariable e : copy) {
				add("param");
				e.apply(this);
				addn();
			}
		}
		if (node.getType() != null) {
			add("type");
			node.getType().apply(this);
			addn();
		}
		if (node.getBody() != null) {
			add("body");
			node.getBody().apply(this);
			addn();
		}
		decEinrueckung();
		outAMethod(node);
	}

	public void inADeclaration(ADeclaration node) {
		defaultIn(node);
	}

	public void outADeclaration(ADeclaration node) {
		defaultOut(node);
	}

	@Override
	public void caseADeclaration(ADeclaration node) {
		inADeclaration(node);
		add(node);
		incEinrueckung();
		if (node.getModifier() != null) {
			add("modifier");
			node.getModifier().apply(this);
		}
		if (node.getVar() != null) {
			add("var");
			node.getVar().apply(this);
		}
		decEinrueckung();
		outADeclaration(node);
	}

	public void inAUnnamedVariable(AUnnamedVariable node) {
		defaultIn(node);
	}

	public void outAUnnamedVariable(AUnnamedVariable node) {
		defaultOut(node);
	}

	@Override
	public void caseAUnnamedVariable(AUnnamedVariable node) {
		inAUnnamedVariable(node);
		add(node);
		incEinrueckung();
		if (node.getType() != null) {
			add("type");
			node.getType().apply(this);
		}
		if (node.getVal() != null) {
			add("val");
			node.getVal().apply(this);
		}
		decEinrueckung();
		outAUnnamedVariable(node);
	}

	public void inANamedVariable(ANamedVariable node) {
		defaultIn(node);
	}

	public void outANamedVariable(ANamedVariable node) {
		defaultOut(node);
	}

	@Override
	public void caseANamedVariable(ANamedVariable node) {
		inANamedVariable(node);
		add(node);
		incEinrueckung();
		if (node.getName() != null) {
			add("name");
			node.getName().apply(this);
		}
		addn();
		if (node.getType() != null) {
			add("type");
			node.getType().apply(this);
		}
		addn();
		if (node.getVal() != null) {
			add("val");
			node.getVal().apply(this);
		}
		decEinrueckung();
		outANamedVariable(node);
	}

	public void inATrueFlag(ATrueFlag node) {
		defaultIn(node);
	}

	public void outATrueFlag(ATrueFlag node) {
		defaultOut(node);
	}

	@Override
	public void caseATrueFlag(ATrueFlag node) {
		inATrueFlag(node);
		add(node);
		outATrueFlag(node);
	}

	public void inAFalseFlag(AFalseFlag node) {
		defaultIn(node);
	}

	public void outAFalseFlag(AFalseFlag node) {
		defaultOut(node);
	}

	@Override
	public void caseAFalseFlag(AFalseFlag node) {
		inAFalseFlag(node);
		add(node);
		outAFalseFlag(node);
	}

	public void inATypeType(ATypeType node) {
		defaultIn(node);
	}

	public void outATypeType(ATypeType node) {
		defaultOut(node);
	}

	@Override
	public void caseATypeType(ATypeType node) {
		inATypeType(node);
		add(node);
		incEinrueckung();
		if (node.getName() != null) {
			add("name");
			node.getName().apply(this);
		}
		addn();
		if (node.getArray() != null) {
			add("array");
			node.getArray().apply(this);
		}
		decEinrueckung();
		outATypeType(node);
	}

	public void inATupleType(ATupleType node) {
		defaultIn(node);
	}

	public void outATupleType(ATupleType node) {
		defaultOut(node);
	}

	@Override
	public void caseATupleType(ATupleType node) {
		add(node);
		incEinrueckung();
		inATupleType(node);
		{
			List<PVariable> copy = new ArrayList<PVariable>(node.getVars());
			for (PVariable e : copy) {
				add("var");
				e.apply(this);
			}
		}
		if (node.getArray() != null) {
			add("array");
			node.getArray().apply(this);
		}
		decEinrueckung();
		outATupleType(node);
	}

	public void inADefaultType(ADefaultType node) {
		defaultIn(node);
	}

	public void outADefaultType(ADefaultType node) {
		defaultOut(node);
	}

	@Override
	public void caseADefaultType(ADefaultType node) {
		inADefaultType(node);
		add(node);
		outADefaultType(node);
	}

	public void inABlock(ABlock node) {
		defaultIn(node);
	}

	public void outABlock(ABlock node) {
		defaultOut(node);
	}

	@Override
	public void caseABlock(ABlock node) {
		inABlock(node);
		add(node);
		incEinrueckung();
		{
			List<PStatement> copy = new ArrayList<PStatement>(node.getStatements());
			for (PStatement e : copy) {
				add("statement");
				e.apply(this);
			}
		}
		decEinrueckung();
		outABlock(node);
	}

	public void inAIfStatement(AIfStatement node) {
		defaultIn(node);
	}

	public void outAIfStatement(AIfStatement node) {
		defaultOut(node);
	}

	@Override
	public void caseAIfStatement(AIfStatement node) {
		inAIfStatement(node);
		add(node);
		incEinrueckung();
		if (node.getCond() != null) {
			add("cond");
			node.getCond().apply(this);
		}
		if (node.getBlock() != null) {
			add("block");
			node.getBlock().apply(this);
		}
		decEinrueckung();
		outAIfStatement(node);
	}

	public void inAWhileStatement(AWhileStatement node) {
		defaultIn(node);
	}

	public void outAWhileStatement(AWhileStatement node) {
		defaultOut(node);
	}

	@Override
	public void caseAWhileStatement(AWhileStatement node) {
		inAWhileStatement(node);
		add(node);
		incEinrueckung();
		if (node.getCond() != null) {
			add("cond");
			node.getCond().apply(this);
		}
		if (node.getBlock() != null) {
			add("block");
			node.getBlock().apply(this);
		}
		decEinrueckung();
		outAWhileStatement(node);
	}

	public void inAAssignmentStatement(AAssignmentStatement node) {
		defaultIn(node);
	}

	public void outAAssignmentStatement(AAssignmentStatement node) {
		defaultOut(node);
	}

	@Override
	public void caseAAssignmentStatement(AAssignmentStatement node) {
		inAAssignmentStatement(node);
		add(node);
		incEinrueckung();
		add("assignment (" + node.getId() + ")");
		node.getVal().apply(this);
		decEinrueckung();
		outAAssignmentStatement(node);
	}

	public void inAExpStatement(AExpStatement node) {
		defaultIn(node);
	}

	public void outAExpStatement(AExpStatement node) {
		defaultOut(node);
	}

	@Override
	public void caseAExpStatement(AExpStatement node) {
		inAExpStatement(node);
		add(node);
		incEinrueckung();
		if (node.getExp() != null) {
			add("exp");
			node.getExp().apply(this);
		}
		decEinrueckung();
		outAExpStatement(node);
	}

	public void inAPlusExp(APlusExp node) {
		defaultIn(node);
	}

	public void outAPlusExp(APlusExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAPlusExp(APlusExp node) {
		inAPlusExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAPlusExp(node);
	}

	public void inAMinusExp(AMinusExp node) {
		defaultIn(node);
	}

	public void outAMinusExp(AMinusExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAMinusExp(AMinusExp node) {
		inAMinusExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAMinusExp(node);
	}

	public void inADivExp(ADivExp node) {
		defaultIn(node);
	}

	public void outADivExp(ADivExp node) {
		defaultOut(node);
	}

	@Override
	public void caseADivExp(ADivExp node) {
		inADivExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outADivExp(node);
	}

	public void inAMultExp(AMultExp node) {
		defaultIn(node);
	}

	public void outAMultExp(AMultExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAMultExp(AMultExp node) {
		inAMultExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAMultExp(node);
	}

	public void inAModExp(AModExp node) {
		defaultIn(node);
	}

	public void outAModExp(AModExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAModExp(AModExp node) {
		inAModExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAModExp(node);
	}

	public void inAAndExp(AAndExp node) {
		defaultIn(node);
	}

	public void outAAndExp(AAndExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAAndExp(AAndExp node) {
		inAAndExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAAndExp(node);
	}

	public void inAOrExp(AOrExp node) {
		defaultIn(node);
	}

	public void outAOrExp(AOrExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAOrExp(AOrExp node) {
		inAOrExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAOrExp(node);
	}

	public void inAXorExp(AXorExp node) {
		defaultIn(node);
	}

	public void outAXorExp(AXorExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAXorExp(AXorExp node) {
		inAXorExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAXorExp(node);
	}

	public void inAEqExp(AEqExp node) {
		defaultIn(node);
	}

	public void outAEqExp(AEqExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAEqExp(AEqExp node) {
		inAEqExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAEqExp(node);
	}

	public void inANeqExp(ANeqExp node) {
		defaultIn(node);
	}

	public void outANeqExp(ANeqExp node) {
		defaultOut(node);
	}

	@Override
	public void caseANeqExp(ANeqExp node) {
		inANeqExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outANeqExp(node);
	}

	public void inALteqExp(ALteqExp node) {
		defaultIn(node);
	}

	public void outALteqExp(ALteqExp node) {
		defaultOut(node);
	}

	@Override
	public void caseALteqExp(ALteqExp node) {
		inALteqExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outALteqExp(node);
	}

	public void inALtExp(ALtExp node) {
		defaultIn(node);
	}

	public void outALtExp(ALtExp node) {
		defaultOut(node);
	}

	@Override
	public void caseALtExp(ALtExp node) {
		inALtExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outALtExp(node);
	}

	public void inAGteqExp(AGteqExp node) {
		defaultIn(node);
	}

	public void outAGteqExp(AGteqExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAGteqExp(AGteqExp node) {
		inAGteqExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAGteqExp(node);
	}

	public void inAGtExp(AGtExp node) {
		defaultIn(node);
	}

	public void outAGtExp(AGtExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAGtExp(AGtExp node) {
		inAGtExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outAGtExp(node);
	}

	public void inADotExp(ADotExp node) {
		defaultIn(node);
	}

	public void outADotExp(ADotExp node) {
		defaultOut(node);
	}

	@Override
	public void caseADotExp(ADotExp node) {
		inADotExp(node);
		add(node);
		incEinrueckung();
		if (node.getL() != null) {
			add("l");
			node.getL().apply(this);
		}
		if (node.getR() != null) {
			add("r");
			node.getR().apply(this);
		}
		decEinrueckung();
		outADotExp(node);
	}

	public void inANumExp(ANumExp node) {
		defaultIn(node);
	}

	public void outANumExp(ANumExp node) {
		defaultOut(node);
	}

	@Override
	public void caseANumExp(ANumExp node) {
		inANumExp(node);
		incEinrueckung();
		if (node.getNum() != null) {
			add(node, node.toString().trim());
			node.getNum().apply(this);
		}
		decEinrueckung();
		outANumExp(node);
	}

	public void inAFnumExp(AFnumExp node) {
		defaultIn(node);
	}

	public void outAFnumExp(AFnumExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAFnumExp(AFnumExp node) {
		inAFnumExp(node);
		incEinrueckung();
		if (node.getFnum() != null) {
			add(node, node.toString().trim());
			node.getFnum().apply(this);
		}
		decEinrueckung();
		outAFnumExp(node);
	}

	public void inAStringExp(AStringExp node) {
		defaultIn(node);
	}

	public void outAStringExp(AStringExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAStringExp(AStringExp node) {
		inAStringExp(node);
		incEinrueckung();
		if (node.getStr() != null) {
			add(node, node.getStr().toString().trim());
			node.getStr().apply(this);
		}
		decEinrueckung();
		outAStringExp(node);
	}

	public void inATupleExp(ATupleExp node) {
		defaultIn(node);
	}

	public void outATupleExp(ATupleExp node) {
		defaultOut(node);
	}

	@Override
	public void caseATupleExp(ATupleExp node) {
		inATupleExp(node);
		add(node);
		incEinrueckung();
		{
			List<PExp> copy = new ArrayList<PExp>(node.getValues());
			for (PExp e : copy) {
				add("value");
				e.apply(this);
			}
		}
		decEinrueckung();
		outATupleExp(node);
	}

	public void inAIdentifierExp(AIdentifierExp node) {
		defaultIn(node);
	}

	public void outAIdentifierExp(AIdentifierExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAIdentifierExp(AIdentifierExp node) {
		inAIdentifierExp(node);
		incEinrueckung();
		if (node.getId() != null) {
			add("id");
			node.getId().apply(this);
		}
		decEinrueckung();
		outAIdentifierExp(node);
	}

	public void inAMethodcallExp(AMethodcallExp node) {
		defaultIn(node);
	}

	public void outAMethodcallExp(AMethodcallExp node) {
		defaultOut(node);
	}

	@Override
	public void caseAMethodcallExp(AMethodcallExp node) {
		inAMethodcallExp(node);
		add(node);
		incEinrueckung();
		if (node.getId() != null) {
			add("id");
			node.getId().apply(this);
		}
		addn();

		List<PExp> copy = new ArrayList<PExp>(node.getArgs());
		for (PExp e : copy) {
			add("args");
			e.apply(this);
		}


		decEinrueckung();
		outAMethodcallExp(node);
	}

	public void inANoneExp(ANoneExp node) {
		defaultIn(node);
	}

	public void outANoneExp(ANoneExp node) {
		defaultOut(node);
	}

	@Override
	public void caseANoneExp(ANoneExp node) {
		inANoneExp(node);
		add(node);
		outANoneExp(node);
	}
	
	@Override
	public void caseTIdentifier(TIdentifier node) {
		String str = node.toString().trim();
		if (rueck) {
			for (int x = 0; x < einrueckung; x++) {
				ausgabe = ausgabe + "\t";
			}
		}
		ausgabe = ausgabe + "TIdentifier[" + node.getLine() + "," + node.getPos() + "]:" + str;
		rueck = false;
	}

    public void inAListExp(AListExp node)
    {
        defaultIn(node);
    }

    public void outAListExp(AListExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAListExp(AListExp node)
    {
        inAListExp(node);
        add(node);
        incEinrueckung();
        {
            List<PExp> copy = new ArrayList<PExp>(node.getList());
            for(PExp e : copy)
            {
                e.apply(this);
            }
        }
        decEinrueckung();
        outAListExp(node);
    }

}
