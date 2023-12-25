package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeAddExp extends  MyTreeNode {
    private final NodeMulExp mulExp1;
    private final NodeAddExp addExp;
    private final wordTuple operator;
    private final NodeMulExp mulExp2;
    private IdentType identType;

    public NodeAddExp(int lineNum, NodeMulExp mulExp1) {
        super(lineNum);
        this.mulExp1 = mulExp1;
        this.addExp = null;
        this.operator = null;
        this.mulExp2 = null;
    }

    public NodeAddExp(int lineNum, NodeAddExp addExp, wordTuple operator, NodeMulExp mulExp2) {
        super(lineNum);
        this.mulExp1 = null;
        this.addExp = addExp;
        this.operator = operator;
        this.mulExp2 = mulExp2;
    }

    public NodeMulExp getMulExp1() {
        return mulExp1;
    }

    public NodeAddExp getAddExp() {
        return addExp;
    }

    public wordTuple getOperator() {
        return operator;
    }

    public NodeMulExp getMulExp2() {
        return mulExp2;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return this.identType;
    }
}
