package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

import java.util.ArrayList;

public class NodeConstDef extends MyTreeNode {
    private final wordTuple ident;
    private final ArrayList<NodeConstExp> constExps;
    private final NodeConstInitVal constInitVal;

    public NodeConstDef(int lineNum, wordTuple ident, ArrayList<NodeConstExp> constExps, NodeConstInitVal constInitVal) {
        super(lineNum);
        this.ident = ident;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
    }

    public wordTuple getIdent() {
        return ident;
    }

    public ArrayList<NodeConstExp> getConstExps() {
        return constExps;
    }

    public NodeConstInitVal getConstInitVal() {
        return constInitVal;
    }
}
