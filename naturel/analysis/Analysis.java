/* This file was generated by SableCC (http://www.sablecc.org/). */

package naturel.analysis;

import naturel.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseStart(Start node);
    void caseANaturel(ANaturel node);
    void caseAOneOptionalIdentifier(AOneOptionalIdentifier node);
    void caseANoneOptionalIdentifier(ANoneOptionalIdentifier node);
    void caseAImport(AImport node);
    void caseAClass(AClass node);
    void caseAInterfaceInterface(AInterfaceInterface node);
    void caseAPublModifier(APublModifier node);
    void caseAPrivModifier(APrivModifier node);
    void caseAProtModifier(AProtModifier node);
    void caseAPublStatModifier(APublStatModifier node);
    void caseAPrivStatModifier(APrivStatModifier node);
    void caseAProtStatModifier(AProtStatModifier node);
    void caseAMethod(AMethod node);
    void caseADeclaration(ADeclaration node);
    void caseAUnnamedVariable(AUnnamedVariable node);
    void caseANamedVariable(ANamedVariable node);
    void caseATrueFlag(ATrueFlag node);
    void caseAFalseFlag(AFalseFlag node);
    void caseATypeType(ATypeType node);
    void caseATupleType(ATupleType node);
    void caseADefaultType(ADefaultType node);
    void caseABlock(ABlock node);
    void caseAIfStatement(AIfStatement node);
    void caseAWhileStatement(AWhileStatement node);
    void caseAAssignmentStatement(AAssignmentStatement node);
    void caseADeclarationStatement(ADeclarationStatement node);
    void caseAExpStatement(AExpStatement node);
    void caseAPlusExp(APlusExp node);
    void caseAMinusExp(AMinusExp node);
    void caseADivExp(ADivExp node);
    void caseAMultExp(AMultExp node);
    void caseAModExp(AModExp node);
    void caseAAndExp(AAndExp node);
    void caseAOrExp(AOrExp node);
    void caseAXorExp(AXorExp node);
    void caseAEqExp(AEqExp node);
    void caseANeqExp(ANeqExp node);
    void caseALteqExp(ALteqExp node);
    void caseALtExp(ALtExp node);
    void caseAGteqExp(AGteqExp node);
    void caseAGtExp(AGtExp node);
    void caseADotExp(ADotExp node);
    void caseANumExp(ANumExp node);
    void caseAFnumExp(AFnumExp node);
    void caseAStringExp(AStringExp node);
    void caseATupleExp(ATupleExp node);
    void caseAIdentifierExp(AIdentifierExp node);
    void caseAMethodcallExp(AMethodcallExp node);
    void caseAListExp(AListExp node);
    void caseAMethodcalldefExp(AMethodcalldefExp node);
    void caseANoneExp(ANoneExp node);

    void caseTComment(TComment node);
    void caseTLPar(TLPar node);
    void caseTRPar(TRPar node);
    void caseTLBracket(TLBracket node);
    void caseTRBracket(TRBracket node);
    void caseTLBrace(TLBrace node);
    void caseTRBrace(TRBrace node);
    void caseTPlusPlus(TPlusPlus node);
    void caseTMinusMinus(TMinusMinus node);
    void caseTHashHash(THashHash node);
    void caseTStarEqual(TStarEqual node);
    void caseTDivEqual(TDivEqual node);
    void caseTModEqual(TModEqual node);
    void caseTPlusEqual(TPlusEqual node);
    void caseTMinusEqual(TMinusEqual node);
    void caseTAmpersandEqual(TAmpersandEqual node);
    void caseTBarEqual(TBarEqual node);
    void caseTPlus(TPlus node);
    void caseTMinus(TMinus node);
    void caseTMult(TMult node);
    void caseTDiv(TDiv node);
    void caseTHash(THash node);
    void caseTSemi(TSemi node);
    void caseTComma(TComma node);
    void caseTAssign(TAssign node);
    void caseTColon(TColon node);
    void caseTMod(TMod node);
    void caseTAmpersand(TAmpersand node);
    void caseTQuestMark(TQuestMark node);
    void caseTBar(TBar node);
    void caseTXor(TXor node);
    void caseTDot(TDot node);
    void caseTEllipsis(TEllipsis node);
    void caseTEq(TEq node);
    void caseTNeq(TNeq node);
    void caseTLteq(TLteq node);
    void caseTLt(TLt node);
    void caseTGteq(TGteq node);
    void caseTGt(TGt node);
    void caseTExclMark(TExclMark node);
    void caseTBlank(TBlank node);
    void caseTAs(TAs node);
    void caseTIf(TIf node);
    void caseTElse(TElse node);
    void caseTWhile(TWhile node);
    void caseTFor(TFor node);
    void caseTUse(TUse node);
    void caseTString(TString node);
    void caseTIdentifier(TIdentifier node);
    void caseTFloatSequence(TFloatSequence node);
    void caseTDigitSequence(TDigitSequence node);
    void caseEOF(EOF node);
}
