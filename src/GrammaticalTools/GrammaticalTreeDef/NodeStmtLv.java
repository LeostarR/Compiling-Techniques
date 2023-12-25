package GrammaticalTools.GrammaticalTreeDef;

public class NodeStmtLv extends NodeStmt {
    private final NodeLVal lVal;
    private final NodeExp exp;

    public NodeStmtLv(int lineNum, NodeLVal lVal, NodeExp exp) {
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
