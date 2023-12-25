package GrammaticalTools.GrammaticalTreeDef;

import java.util.ArrayList;

public class NodeConstInitVal extends MyTreeNode {
    private final NodeConstExp constExp;
    private final ArrayList<NodeConstInitVal> constInitVals;

    public NodeConstInitVal(int lineNum, NodeConstExp constExp) {
        super(lineNum);
        this.constExp = constExp;
        this.constInitVals = null;
    }

    public NodeConstInitVal(int lineNum, ArrayList<NodeConstInitVal> constInitVals) {
        super(lineNum);
        this.constExp = null;
        this.constInitVals = constInitVals;
    }

    public NodeConstInitVal(int lineNum) {
        super(lineNum);
        this.constExp = null;
        this.constInitVals = null;
    }

    public NodeConstExp getConstExp() {
        return constExp;
    }

    public ArrayList<NodeConstInitVal> getConstInitVals() {
        return constInitVals;
    }
}
