package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeLAndExp extends MyTreeNode {
    private final NodeEqExp eqExp1;
    private final NodeLAndExp lAndExp;
    private final wordTuple operator;
    private final NodeEqExp eqExp2;
    private IdentType identType;

    public NodeLAndExp(int lineNum, NodeEqExp eqExp1) {
        super(lineNum);
        this.eqExp1 = eqExp1;
        this.lAndExp = null;
        this.operator = null;
        this.eqExp2 = null;
    }

    public NodeLAndExp(int lineNum, NodeLAndExp lAndExp, wordTuple operator, NodeEqExp eqExp2) {
        super(lineNum);
        this.eqExp1 = null;
        this.lAndExp = lAndExp;
        this.operator = operator;
        this.eqExp2 = eqExp2;
    }

    public NodeEqExp getEqExp1() {
        return eqExp1;
    }

    public NodeLAndExp getlAndExp() {
        return lAndExp;
    }

    public wordTuple getOperator() {
        return operator;
    }

    public NodeEqExp getEqExp2() {
        return eqExp2;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return identType;
    }
}
