package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

import java.util.ArrayList;

public class NodeFuncFParam extends MyTreeNode {
    private final NodeBType bType;
    private final wordTuple ident;
    private final ArrayList<NodeConstExp> constExps;

    public NodeFuncFParam(int lineNum, NodeBType bType, wordTuple ident) {
        super(lineNum);
        this.bType = bType;
        this.ident = ident;
        this.constExps = null;
    }

    public NodeFuncFParam(int lineNum, NodeBType bType, wordTuple ident, ArrayList<NodeConstExp> constExps) {
        super(lineNum);
        this.bType = bType;
        this.ident = ident;
        this.constExps = constExps;
    }

    public NodeBType getbType() {
        return bType;
    }

    public wordTuple getIdent() {
        return ident;
    }

    public ArrayList<NodeConstExp> getConstExps() {
        return constExps;
    }

    public IdentType getIdentType() {
        if (this.constExps == null) {
            return IdentType.COMMONVAR;
        } else {
            if (this.constExps.size() == 1) {
                return IdentType.TWODIMARR;
            } else {
                return IdentType.ONEDIMARR;
            }
        }
    }
}
