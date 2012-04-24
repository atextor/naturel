package naturel;

import java.util.*;
import naturel.analysis.AnalysisAdapter;
import naturel.node.*;

public class PrettyPrinter extends AnalysisAdapter {
	private String ausgabe = "";
	private int einrueckung = 0;
	private boolean jetzteinruecken = false;

	private void add(String str) {
		if (jetzteinruecken) {
			for (int x = 0; x < einrueckung; x++) {
				ausgabe = ausgabe + "\t";
			}
		}
		ausgabe = ausgabe + str;
		jetzteinruecken = str.contains("\n");
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
		{
			List<PImport> copy = new ArrayList<PImport>(node.getUses());
			for (PImport e : copy) {
				e.apply(this);
			}
		}
		add("\n");
		{
			List<PClass> copy = new ArrayList<PClass>(node.getClasses());
			for (PClass e : copy) {
				e.apply(this);
				add("\n");
			}
		}
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
		if (node.getId() != null) {
			add(node.getId().toString().trim());
			node.getId().apply(this);
		}
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
		add("use ");
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getName());
			boolean erster = true;
			for (TIdentifier e : copy) {
				if (erster) {
					erster = false;
				} else {
					add(".");
				}
				add(e.toString().trim());
				e.apply(this);
			}
		}
		if (node.getAs() != null && (node.getAs() instanceof AOneOptionalIdentifier)) {
			add(" as ");
			node.getAs().apply(this);
		}
		add(";\n");
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
		
		int numVars = 0;
		int numMethods = 0;
		
		if (node.getModifier() != null) {
			node.getModifier().apply(this);
		}
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getName());
			boolean erster = true;
			for (TIdentifier e : copy) {
				if (erster) {
					erster = false;
				} else {
					add(".");
				}
				add(e.toString().trim());
				e.apply(this);
			}
		}
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getSuper());
			boolean erster = true;
			for (TIdentifier e : copy) {
				if (erster) {
					erster = false;
					add(" < ");
				} else {
					add(".");
				}
				add(e.toString().trim());
				e.apply(this);
			}
		}
		{
			List<PInterface> copy = new ArrayList<PInterface>(node.getInterfaces());
			boolean erster = true;
			for (PInterface e : copy) {
				if (erster) {
					erster = false;
					add(" : ");
				} else {
					add(", ");
				}
				e.apply(this);
			}
		}
		add(" {\n");
		incEinrueckung();
		{
			List<PDeclaration> copy = new ArrayList<PDeclaration>(node.getVars());
			for (PDeclaration e : copy) {
				numVars++;
				e.apply(this);
				add(";\n");
			}
		}
		
		if (numVars > 0 && node.getMethods().size() > 0) {
			add("\n");
		}
		
		{
			List<PMethod> copy = new ArrayList<PMethod>(node.getMethods());
			for (PMethod e : copy) {
				if (numMethods > 0) {
					add("\n");
				}
				numMethods++;
				e.apply(this);
			}
		}
		decEinrueckung();
		add("}\n");
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
		{
			List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getName());
			boolean erster = true;
			for (TIdentifier e : copy) {
				if (erster) {
					erster = false;
				} else {
					add(".");
				}
				add(e.toString().trim());
				e.apply(this);
			}
		}
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
		add("+");
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
		add("-");
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
		add("#");
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
		add("++");
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
		add("--");
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
		add("##");
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
		if (node.getModifier() != null) {
			node.getModifier().apply(this);
		}
		if (node.getName() != null) {
			add(node.getName().toString().trim());
			node.getName().apply(this);
		}
		{
			add("(");
			List<PVariable> copy = new ArrayList<PVariable>(node.getParams());
			boolean erster = true;
			for (PVariable e : copy) {
				if (erster) {
					erster = false;
				} else {
					add(", ");
				}
				e.apply(this);
			}
			add(")");
		}
		if (node.getType() != null && !(node.getType() instanceof ADefaultType)) {
			add(":");
			node.getType().apply(this);
		}
		if (node.getBody() != null) {
			node.getBody().apply(this);
		}
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
		if (node.getModifier() != null) {
			node.getModifier().apply(this);
		}
		if (node.getVar() != null) {
			node.getVar().apply(this);
		}
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
		if (node.getType() != null) {
			add(":");
			node.getType().apply(this);
		}
		if (node.getVal() != null && !(node.getVal() instanceof ANoneExp)) {
			add(" := ");
			node.getVal().apply(this);
		}
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
		if (node.getName() != null) {
			add(node.getName().toString().trim());
			node.getName().apply(this);
		}
		if (node.getType() != null) {
			add(":");
			node.getType().apply(this);
		}
		if (node.getVal() != null && !(node.getVal() instanceof ANoneExp)) {
			add(" := ");
			node.getVal().apply(this);
		}
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
		if (node.getName() != null) {
			add(node.getName().toString().trim());
			node.getName().apply(this);
		}
		if (node.getArray() != null) {
			if (node.getArray() instanceof ATrueFlag) {
				add("[]");
			}
			node.getArray().apply(this);
		}
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
		add("(");
		inATupleType(node);
		{
			List<PVariable> copy = new ArrayList<PVariable>(node.getVars());
			boolean erster = true;
			for (PVariable e : copy) {
				if (erster) {
					erster = false;
				} else {
					add(", ");
				}
				e.apply(this);
			}
		}
		add(")");
		if (node.getArray() != null && (node.getArray() instanceof ATrueFlag)) {
			add("[]");
			node.getArray().apply(this);
		}
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
		add(" {\n");
		incEinrueckung();
		{
			List<PStatement> copy = new ArrayList<PStatement>(node.getStatements());
			for (PStatement e : copy) {
				e.apply(this);
				if (!(e instanceof AIfStatement) && !(e instanceof AWhileStatement)) {
					// Ifs und Whiles brauchen kein Semikolon
					add(";\n");
				}
			}
		}
		decEinrueckung();
		add("}\n");
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
		add("if (");
		if (node.getCond() != null) {
			node.getCond().apply(this);
		}
		add(")");
		if (node.getBlock() != null) {
			node.getBlock().apply(this);
		}
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
		add("while (");
		if (node.getCond() != null) {
			node.getCond().apply(this);
		}
		add(")");
		if (node.getBlock() != null) {
			node.getBlock().apply(this);
		}
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
		add(node.getId() + " := ");
		if (node.getVal() != null) {
			node.getVal().apply(this);
		}
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
		if (node.getExp() != null) {
			node.getExp().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" + ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" - ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" / ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" * ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" % ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" & ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" | ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" ^ ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" = ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" != ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" <= ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" < ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" >= ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(" > ");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getL() != null) {
			node.getL().apply(this);
		}
		add(".");
		if (node.getR() != null) {
			node.getR().apply(this);
		}
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
		if (node.getNum() != null) {
			add(node.toString().trim());
			node.getNum().apply(this);
		}
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
		if (node.getFnum() != null) {
			add(node.toString().trim());
			node.getFnum().apply(this);
		}
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
		if (node.getStr() != null) {
			add(node.getStr().toString().trim());
			node.getStr().apply(this);
		}
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
		{
			add("(");
			boolean erster = true;
			List<PExp> copy = new ArrayList<PExp>(node.getValues());
			for (PExp e : copy) {
				if (!erster) {
					add(", ");
				} else {
					erster = !erster;
				}
				e.apply(this);
			}
			add(")");
		}
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
		if (node.getId() != null) {
			node.getId().apply(this);
		}
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
		if (node.getId() != null) {
			add(node.getId().toString().trim());
			node.getId().apply(this);
		}
		if (node.getArgs().size() > 0) {
			add("(");
			List<PExp> copy = new ArrayList<PExp>(node.getArgs());
			if (!(copy.get(0) instanceof ANoneExp)) {
				boolean erster = true;
				for (PExp e : copy) {
					if (!erster) {
						add(", ");
					} else {
						erster = !erster;
					}
					e.apply(this);
				}
			}
			add(")");
		}
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
		outANoneExp(node);
	}
}
