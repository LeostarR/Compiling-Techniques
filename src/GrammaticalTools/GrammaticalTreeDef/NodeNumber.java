package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeNumber extends MyTreeNode {
    private final wordTuple intConst;
    private IdentType identType;

    public NodeNumber(int lineNum, wordTuple intConst) {
        super(lineNum);
        this.intConst = intConst;
    }

    public wordTuple getIntConst() {
        return intConst;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return this.identType;
    }
}
