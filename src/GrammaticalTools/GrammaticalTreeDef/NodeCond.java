package GrammaticalTools.GrammaticalTreeDef;

public class NodeCond extends MyTreeNode {
    private final NodeLOrExp lOrExp;

    public NodeCond(int lineNum, NodeLOrExp lOrExp) {
        super(lineNum);
        this.lOrExp = lOrExp;
    }

    public NodeLOrExp getlOrExp() {
        return lOrExp;
    }
}
