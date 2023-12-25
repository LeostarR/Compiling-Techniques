package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

import java.util.ArrayList;

public class NodeStmtPr extends NodeStmt {
    private final wordTuple funName;
    private final NodeFormatString formatString;
    private final ArrayList<NodeExp> exps;

    public NodeStmtPr(int lineNum, wordTuple funName, NodeFormatString formatString, ArrayList<NodeExp> exps) {
        super(lineNum);
        this.funName = funName;
        this.formatString = formatString;
        this.exps = exps;
    }

    public wordTuple getFunName() {
        return funName;
    }

    public NodeFormatString getFormatString() {
        return formatString;
    }

    public ArrayList<NodeExp> getExp() {
        return exps;
    }
}
