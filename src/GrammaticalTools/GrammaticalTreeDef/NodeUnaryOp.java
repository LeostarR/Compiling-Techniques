package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

public class NodeUnaryOp extends  MyTreeNode {
    private final wordTuple operator;

    public NodeUnaryOp(int lineNum, wordTuple operator) {
        super(lineNum);
        this.operator = operator;
    }

    public wordTuple getOperator() {
        return operator;
    }
}
