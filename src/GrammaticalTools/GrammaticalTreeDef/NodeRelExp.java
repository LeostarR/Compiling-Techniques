package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeRelExp extends MyTreeNode {
    private final NodeAddExp addExp1;
    private final NodeRelExp relExp;
    private final wordTuple operator;
    private final NodeAddExp addExp2;
    private IdentType identType;

    public NodeRelExp(int lineNum, NodeAddExp addExp1) {
        super(lineNum);
        this.addExp1 = addExp1;
        this.relExp = null;
        this.operator = null;
        this.addExp2 = null;
    }

    public NodeRelExp(int lineNum, NodeRelExp relExp, wordTuple operator, NodeAddExp addExp2) {
        super(lineNum);
        this.addExp1 = null;
        this.relExp = relExp;
        this.operator = operator;
        this.addExp2 = addExp2;
    }

    public NodeAddExp getAddExp1() {
        return addExp1;
    }

    public NodeRelExp getRelExp() {
        return relExp;
    }

    public wordTuple getOperator() {
        return operator;
    }

    public NodeAddExp getAddExp2() {
        return addExp2;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return identType;
    }
}
