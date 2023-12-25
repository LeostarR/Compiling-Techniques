package GrammaticalTools.GrammaticalTreeDef;

import SymbolTools.IdentType;

public class NodePrimaryExp extends MyTreeNode {
    private final NodeExp exp;
    private final NodeLVal lVal;
    private final NodeNumber number;
    private IdentType identType;

    public NodePrimaryExp(int lineNum, NodeExp exp) {
        super(lineNum);
        this.exp = exp;
        this.lVal = null;
        this.number = null;
    }

    public NodePrimaryExp(int lineNum, NodeLVal lVal) {
        super(lineNum);
        this.exp = null;
        this.lVal = lVal;
        this.number = null;
    }

    public NodePrimaryExp(int lineNum, NodeNumber number) {
        super(lineNum);
        this.exp = null;
        this.lVal = null;
        this.number = number;
    }

    public NodeExp getExp() {
        return exp;
    }

    public NodeLVal getlVal() {
        return lVal;
    }

    public NodeNumber getNumber() {
        return number;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return this.identType;
    }
}
