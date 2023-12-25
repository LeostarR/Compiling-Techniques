package GrammaticalTools.GrammaticalTreeDef;

public class NodeStmtRe extends  NodeStmt {
    private final NodeExp exp;

    public NodeStmtRe(int lineNum, NodeExp exp) {
        super(lineNum);
        this.exp = exp;
    }

    public NodeExp getExp() {
        return exp;
    }
}
