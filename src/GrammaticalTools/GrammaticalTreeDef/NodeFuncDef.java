package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

public class NodeFuncDef extends MyTreeNode {
    private final NodeFuncType funcType;
    private final wordTuple ident;
    private final NodeFuncFParams funcFParams;
    private final NodeBlock block;

    public NodeFuncDef(int lineNum, NodeFuncType funcType, wordTuple ident, NodeFuncFParams funcFParams, NodeBlock block) {
        super(lineNum);
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }

    public NodeFuncDef(int lineNum, NodeFuncType funcType, wordTuple ident, NodeBlock block) {
        super(lineNum);
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = null;
        this.block = block;
    }

    public NodeFuncType getFuncType() {
        return funcType;
    }

    public wordTuple getIdent() {
        return ident;
    }

    public NodeFuncFParams getFuncFParams() {
        return funcFParams;
    }

    public NodeBlock getBlock() {
        return block;
    }
}
