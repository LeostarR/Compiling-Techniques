package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

import java.util.ArrayList;

public class NodeLVal extends MyTreeNode {
    private final wordTuple ident;
    private final ArrayList<NodeExp> nodeExps;
    private IdentType identType;

    public NodeLVal(int lineNum, wordTuple ident, ArrayList<NodeExp> nodeExps) {
        super(lineNum);
        this.ident = ident;
        this.nodeExps = nodeExps;
    }

    public wordTuple getIdent() {
        return ident;
    }

    public ArrayList<NodeExp> getNodeExps() {
        return nodeExps;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return this.identType;
    }
}
