package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

public class NodeStmtGe extends NodeStmt {
    private final  NodeLVal lVal;
    private final wordTuple funName;

    public NodeStmtGe(int lineNum, NodeLVal lVal, wordTuple funName) {
        super(lineNum);
        this.lVal = lVal;
        this.funName = funName;
    }

    public NodeLVal getlVal() {
        return lVal;
    }

    public wordTuple getFunName() {
        return funName;
    }
}
