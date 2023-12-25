package GrammaticalTools.GrammaticalTreeDef;

public class NodeStmtEx extends NodeStmt {
    private final NodeExp exp;

    public NodeStmtEx(int lineNum, NodeExp exp) {
        super(lineNum);
        this.exp = exp;
    }

    public NodeExp getExp() {
        return exp;
    }
}
