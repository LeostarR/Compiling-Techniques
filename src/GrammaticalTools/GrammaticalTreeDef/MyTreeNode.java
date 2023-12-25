package GrammaticalTools.GrammaticalTreeDef;

public class MyTreeNode {
    private int lineNum;

    public MyTreeNode(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return this.lineNum;
    }

    public void changeLine(int newLineNum) {
        this.lineNum = newLineNum;
    }
}
