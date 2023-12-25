package GrammaticalTools.GrammaticalTreeDef;

public class NodeForStmt extends MyTreeNode {
    private final NodeLVal lVal;
    private final NodeExp exp;

    public NodeForStmt(int lineNum, NodeLVal lVal, NodeExp exp) {
        super(lineNum);
        this.lVal = lVal;
        this.exp = exp;
    }

    public NodeLVal getlVal() {
        return lVal;
    }

    public NodeExp getExp() {
        return exp;
    }
}
