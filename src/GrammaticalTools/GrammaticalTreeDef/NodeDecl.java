package GrammaticalTools.GrammaticalTreeDef;

public class NodeDecl extends MyTreeNode{
    private final NodeConstDecl constDecl;
    private final NodeVarDecl varDecl;

    public NodeDecl(int lineNum, NodeConstDecl constDecl) {
        super(lineNum);
        this.constDecl = constDecl;
        this.varDecl = null;
    }

    public NodeDecl(int lineNum, NodeVarDecl varDecl) {
        super(lineNum);
        this.constDecl = null;
        this.varDecl = varDecl;
    }

    public NodeConstDecl getConstDecl() {
        return constDecl;
    }

    public NodeVarDecl getVarDecl() {
        return varDecl;
    }
}
