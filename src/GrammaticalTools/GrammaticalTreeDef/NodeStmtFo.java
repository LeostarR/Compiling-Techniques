package GrammaticalTools.GrammaticalTreeDef;

public class NodeStmtFo extends NodeStmt {
    private final NodeForStmt forStmt1;
    private final NodeCond cond;
    private final NodeForStmt forStmt2;
    private final NodeStmt stmt;

    public NodeStmtFo(int lineNum, NodeForStmt forStmt1, NodeCond cond, NodeForStmt forStmt2, NodeStmt stmt) {
        super(lineNum);
        this.forStmt1 = forStmt1;
        this.cond = cond;
        this.forStmt2 = forStmt2;
        this.stmt = stmt;
    }

    public NodeForStmt getForStmt1() {
        return forStmt1;
    }

    public NodeCond getCond() {
        return cond;
    }

    public NodeForStmt getForStmt2() {
        return forStmt2;
    }

    public NodeStmt getStmt() {
        return stmt;
    }
}

