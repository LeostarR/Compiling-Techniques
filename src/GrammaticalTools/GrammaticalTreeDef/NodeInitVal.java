package GrammaticalTools.GrammaticalTreeDef;

import java.util.ArrayList;

public class NodeInitVal extends MyTreeNode {
    private final NodeExp exp;
    private final ArrayList<NodeInitVal> initVals;

    public NodeInitVal(int lineNum, NodeExp exp) {
        super(lineNum);
        this.exp = exp;
        this.initVals = null;
    }

    public NodeInitVal(int lineNum, ArrayList<NodeInitVal> initVals) {
        super(lineNum);
        this.exp = null;
        this.initVals = initVals;
    }

    public NodeInitVal(int lineNum) {
        super(lineNum);
        this.exp = null;
        this.initVals = null;
    }

    public NodeExp getExp() {
        return exp;
    }

    public ArrayList<NodeInitVal> getInitVals() {
        return initVals;
    }
}
