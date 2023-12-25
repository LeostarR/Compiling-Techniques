package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;
import SymbolTools.IdentType;

public class NodeUnaryExp extends MyTreeNode {
    private final NodePrimaryExp primaryExp;
    private final wordTuple ident;
    private final NodeFuncRParams funcRParams;
    private final NodeUnaryOp unaryOp;
    private final NodeUnaryExp unaryExp;
    private IdentType identType;

    public NodeUnaryExp(int lineNum, NodePrimaryExp primaryExp) {
        super(lineNum);
        this.primaryExp = primaryExp;
        this.ident = null;
        this.funcRParams = null;
        this.unaryOp = null;
        this.unaryExp = null;
    }

    public NodeUnaryExp(int lineNum, wordTuple ident, NodeFuncRParams funcRParams) {
        super(lineNum);
        this.primaryExp = null;
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.unaryOp = null;
        this.unaryExp = null;
    }

    public NodeUnaryExp(int lineNum, NodeUnaryOp unaryOp, NodeUnaryExp unaryExp) {
        super(lineNum);
        this.primaryExp = null;
        this.ident = null;
        this.funcRParams = null;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    public NodePrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public wordTuple getIdent() {
        return ident;
    }

    public NodeFuncRParams getFuncRParams() {
        return funcRParams;
    }

    public NodeUnaryOp getUnaryOp() {
        return unaryOp;
    }

    public NodeUnaryExp getUnaryExp() {
        return unaryExp;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public IdentType getIdentType() {
        return this.identType;
    }
}
