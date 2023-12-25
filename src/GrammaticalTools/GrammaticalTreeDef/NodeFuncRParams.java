package GrammaticalTools.GrammaticalTreeDef;

import SymbolTools.IdentType;

import java.util.ArrayList;

public class NodeFuncRParams extends  MyTreeNode {
    private final ArrayList<NodeExp> exps;
    private final ArrayList<IdentType> typeList = new ArrayList<>();

    public NodeFuncRParams(int lineNum, ArrayList<NodeExp> exps) {
        super(lineNum);
        this.exps = exps;
    }

    public ArrayList<NodeExp> getExp() {
        return exps;
    }

    public int getRParamsSize() {
        return this.exps.size();
    }

    public void setTypeList() {
        for (NodeExp exp : this.exps) {
            IdentType type = exp.getIdentType();
            this.typeList.add(type);
        }
    }

    public ArrayList<IdentType> getTypeList() {
        return this.typeList;
    }
}
