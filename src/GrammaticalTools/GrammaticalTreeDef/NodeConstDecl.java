package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

import java.util.ArrayList;

public class NodeConstDecl extends MyTreeNode {
    private final wordTuple constType;
    private final NodeBType bType;
    private final ArrayList<NodeConstDef> constDefs;

    public NodeConstDecl(int lineNum, wordTuple constType, NodeBType bType, ArrayList<NodeConstDef> constDefs) {
        super(lineNum);
        this.constType = constType;
        this.bType = bType;
        this.constDefs = constDefs;
    }

    public wordTuple getConstType() {
        return constType;
    }

    public NodeBType getBType() {
        return bType;
    }

    public ArrayList<NodeConstDef> getConstDefs() {
        return constDefs;
    }
}
