package GrammaticalTools.GrammaticalTreeDef;

public class NodeConstExp extends MyTreeNode {
    private final NodeAddExp addExp;

    public NodeConstExp(int lineNum, NodeAddExp addExp) {
        super(lineNum);
        this.addExp = addExp;
    }

    public NodeAddExp getAddExp() {
        return addExp;
    }
}
