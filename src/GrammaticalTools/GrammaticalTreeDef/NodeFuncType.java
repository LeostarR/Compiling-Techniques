package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

public class NodeFuncType extends MyTreeNode {
    private final wordTuple type;

    public NodeFuncType(int lineNum, wordTuple type) {
        super(lineNum);
        this.type = type;
    }

    public wordTuple getType() {
        return type;
    }
}
