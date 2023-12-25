package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeEqExp extends MyTreeNode {
    private final NodeRelExp relExp1;
    private final NodeEqExp eqExp;
    private final wordTuple operator;
    private final NodeRelExp relExp2;
    private IdentType identType;

    public NodeEqExp(int lineNum, NodeRelExp relExp1) {
        super(lineNum);
        this.relExp1 = relExp1;
        this.eqExp = null;
        this.operator = null;
        this.relExp2 = null;
    }

    public NodeEqExp(int lineNum, NodeEqExp eqExp, wordTuple operator, NodeRelExp relExp2) {
        super(lineNum);
        this.relExp1 = null;
        this.eqExp = eqExp;
        this.operator = operator;
        this.relExp2 = relExp2;
    }

    public NodeRelExp getRelExp1() {
        return relExp1;
    }

    public NodeEqExp getEqExp() {
        return eqExp;
    }

    public wordTuple getOperator() {
        return operator;
    }

    public NodeRelExp getRelExp2() {
        return relExp2;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return identType;
    }
}
