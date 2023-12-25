package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

public class NodeMainFuncDef extends MyTreeNode {
    private final wordTuple consInt;
    private final wordTuple consMain;
    private final NodeBlock block;

    public NodeMainFuncDef(int lineNum, wordTuple consInt, wordTuple consMain, NodeBlock block) {
        super(lineNum);
        this.consInt = consInt;
        this.consMain = consMain;
        this.block = block;
    }

    public wordTuple getConsInt() {
        return consInt;
    }

    public wordTuple getConsMain() {
        return consMain;
    }

    public NodeBlock getBlock() {
        return block;
    }
}
