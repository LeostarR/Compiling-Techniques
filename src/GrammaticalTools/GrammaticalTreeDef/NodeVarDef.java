package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

import java.util.ArrayList;

public class NodeVarDef extends MyTreeNode {
    private final wordTuple ident;
    private final ArrayList<NodeConstExp> constExps;
    private final NodeInitVal initVal;

    public NodeVarDef(int lineNum, wordTuple ident, ArrayList<NodeConstExp> constExps) {
        super(lineNum);
        this.ident = ident;
        this.constExps = constExps;
        this.initVal = null;
    }

    public NodeVarDef(int lineNum, wordTuple ident, ArrayList<NodeConstExp> constExps, NodeInitVal initVal) {
        super(lineNum);
        this.ident = ident;
        this.constExps = constExps;
        this.initVal = initVal;
    }

    public wordTuple getIdent() {
        return ident;
    }

    public ArrayList<NodeConstExp> getConstExps() {
        return constExps;
    }

    public NodeInitVal getInitVal() {
        return initVal;
    }
}
