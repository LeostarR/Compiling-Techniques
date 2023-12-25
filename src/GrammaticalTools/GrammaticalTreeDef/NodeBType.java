package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

public class NodeBType extends MyTreeNode {
    private final wordTuple constant;

    public NodeBType(int lineNum, wordTuple constant) {
        super(lineNum);
        this.constant = constant;
    }

    public wordTuple getConstant() {
        return constant;
    }
}
