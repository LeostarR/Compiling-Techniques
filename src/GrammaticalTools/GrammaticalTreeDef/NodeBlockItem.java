package GrammaticalTools.GrammaticalTreeDef;

public class NodeBlockItem extends MyTreeNode {
    private final NodeDecl decl;
    private final NodeStmt stmt;

    public NodeBlockItem(int lineNum, NodeDecl decl) {
        super(lineNum);
        this.decl = decl;
        this.stmt = null;
    }

    public NodeBlockItem(int lineNum, NodeStmt stmt) {
        super(lineNum);
        this.decl = null;
        this.stmt = stmt;
    }

    public NodeDecl getDecl() {
        return decl;
    }

    public NodeStmt getStmt() {
        return stmt;
    }

    public boolean isReturnStmt() {
        if (this.decl != null) {
            return false;
        } else {
            return stmt instanceof NodeStmtRe;
        }
    }
}
