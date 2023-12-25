package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeLOrExp extends MyTreeNode {
    private final NodeLAndExp lAndExp1;
    private final NodeLOrExp lOrExp;
    private final wordTuple operator;
    private final NodeLAndExp lAndExp2;
    private IdentType identType;

    public NodeLOrExp(int lineNum, NodeLAndExp lAndExp1) {
        super(lineNum);
        this.lAndExp1 = lAndExp1;
        this.lOrExp = null;
        this.operator = null;
        this.lAndExp2 = null;
    }

    public NodeLOrExp(int lineNum, NodeLOrExp lOrExp, wordTuple operator, NodeLAndExp lAndExp2) {
        super(lineNum);
        this.lAndExp1 = null;
        this.lOrExp = lOrExp;
        this.operator = operator;
        this.lAndExp2 = lAndExp2;
    }

    public NodeLAndExp getlAndExp1() {
        return lAndExp1;
    }

    public NodeLOrExp getlOrExp() {
        return lOrExp;
    }

    public wordTuple getOperator() {
        return operator;
    }

    public NodeLAndExp getlAndExp2() {
        return lAndExp2;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return identType;
    }
}
