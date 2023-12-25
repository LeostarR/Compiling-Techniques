package GrammaticalTools.GrammaticalTreeDef;

import java.util.ArrayList;

public class NodeCompUnit extends MyTreeNode {
    private final ArrayList<NodeDecl> decls;
    private final ArrayList<NodeFuncDef> funcDefs;
    private final NodeMainFuncDef mainFuncDef;

    public NodeCompUnit(int lineNum, ArrayList<NodeDecl> decls, ArrayList<NodeFuncDef> funcDefs,  NodeMainFuncDef mainFuncDef) {
        super(lineNum);
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }

    public ArrayList<NodeDecl> getDecls() {
        return decls;
    }

    public ArrayList<NodeFuncDef> getFuncDefs() {
        return funcDefs;
    }

    public NodeMainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }
}
