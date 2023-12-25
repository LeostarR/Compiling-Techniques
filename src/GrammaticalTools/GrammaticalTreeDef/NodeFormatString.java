package GrammaticalTools.GrammaticalTreeDef;

import LexicalTools.wordTuple;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeFormatString extends MyTreeNode {
    private final wordTuple formatString;

    public NodeFormatString(int lineNum, wordTuple formatString) {
        super(lineNum);
        this.formatString = formatString;
    }

    public wordTuple getFormatString() {
        return formatString;
    }

    public boolean isLegitimate() {
        return this.formatString.isLegitimate();
    }

    public int getFormNum() {
        String s = this.formatString.getWordName();
        Pattern regex = Pattern.compile("%d");
        Matcher matcher = regex.matcher(s);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
