package GrammaticalTools.GrammaticalTreeDef;

import java.util.ArrayList;

public class NodeVarDecl extends MyTreeNode {
    private final NodeBType bType;
    private final ArrayList<NodeVarDef> varDefs;

    public NodeVarDecl(int lineNum, NodeBType bType, ArrayList<NodeVarDef> varDefs) {
        super(lineNum);
        this.bType = bType;
        this.varDefs = varDefs;
    }

    public NodeBType getBType() {
        return bType;
    }

    public ArrayList<NodeVarDef> getVarDefs() {
        return varDefs;
    }
}
