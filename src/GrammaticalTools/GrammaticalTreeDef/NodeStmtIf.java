package GrammaticalTools.GrammaticalTreeDef;

public class NodeStmtIf extends NodeStmt{
    private final NodeCond cond;
    private final NodeStmt stmtIf;
    private final NodeStmt stmtElse;

    public NodeStmtIf(int lineNum, NodeCond cond, NodeStmt stmtIf, NodeStmt stmtElse) {
        super(lineNum);
        this.cond = cond;
        this.stmtIf = stmtIf;
        this.stmtElse = stmtElse;
    }

    public NodeCond getCond() {
        return cond;
    }

    public NodeStmt getStmtIf() {
        return stmtIf;
    }

    public NodeStmt getStmtElse() {
        return stmtElse;
    }
}
