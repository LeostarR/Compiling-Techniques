package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeMulExp extends MyTreeNode {
    private final NodeUnaryExp unaryExp1;
    private final NodeMulExp mulExp;
    private final wordTuple operator;
    private final NodeUnaryExp unaryExp2;
    private IdentType identType;

    public NodeMulExp(int lineNum, NodeUnaryExp unaryExp1) {
        super(lineNum);
        this.unaryExp1 = unaryExp1;
        this.mulExp = null;
        this.operator = null;
        this.unaryExp2 = null;
    }

    public NodeMulExp(int lineNum, NodeMulExp mulExp, wordTuple operator, NodeUnaryExp unaryExp2) {
        super(lineNum);
        this.unaryExp1 = null;
        this.mulExp = mulExp;
        this.operator = operator;
        this.unaryExp2 = unaryExp2;
    }

    public NodeUnaryExp getUnaryExp1() {
        return unaryExp1;
    }

    public NodeMulExp getMulExp() {
        return mulExp;
    }

    public wordTuple getOperator() {
        return operator;
    }

    public NodeUnaryExp getUnaryExp2() {
        return unaryExp2;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return this.identType;
    }
}
