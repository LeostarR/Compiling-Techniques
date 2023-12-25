package GrammaticalTools.GrammaticalTreeDef;

import SymbolTools.IdentType;

import java.util.ArrayList;

public class NodeFuncFParams extends MyTreeNode {
    private final ArrayList<NodeFuncFParam> funcFParams;

    public NodeFuncFParams(int lineNum, ArrayList<NodeFuncFParam> funcFParams) {
        super(lineNum);
        this.funcFParams = funcFParams;
    }

    public ArrayList<NodeFuncFParam> getFuncFParams() {
        return funcFParams;
    }

    public int getParamNum() {
        return this.funcFParams.size();
    }

    public ArrayList<IdentType> getParamTypeList() {
        ArrayList<IdentType> list = new ArrayList<>();
        for (NodeFuncFParam funcFParam : funcFParams) {
            IdentType type = funcFParam.getIdentType();
            list.add(type);
        }
        return list;
    }
}
