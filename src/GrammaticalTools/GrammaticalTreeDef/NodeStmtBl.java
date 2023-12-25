package GrammaticalTools.GrammaticalTreeDef;

public class NodeStmtBl extends NodeStmt {
    private final NodeBlock block;

    public NodeStmtBl(int lineNum, NodeBlock block) {
        super(lineNum);
        this.block = block;
    }

    public NodeBlock getBlock() {
        return block;
    }
}
