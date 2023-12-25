package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

public class NodeStmtBr extends NodeStmt {
    private final wordTuple type;

    public NodeStmtBr(int lineNum, wordTuple type) {
        super(lineNum);
        this.type = type;
    }

    public wordTuple getType() {
        return type;
    }
}
