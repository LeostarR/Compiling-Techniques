package GrammaticalTools.GrammaticalTreeDef;

import SymbolTools.IdentType;

public class NodeExp extends MyTreeNode {
    private final NodeAddExp addExp;
    private IdentType identType;

    public NodeExp(int lineNum, NodeAddExp addExp) {
        super(lineNum);
        this.addExp = addExp;
    }

    public NodeAddExp getAddExp() {
        return addExp;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return this.identType;
    }
}
