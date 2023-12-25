package GrammaticalTools.GrammaticalTreeDef;

import java.util.ArrayList;

public class NodeBlock extends MyTreeNode {
    private final ArrayList<NodeBlockItem> blockItems;

    public NodeBlock(int lineNum, ArrayList<NodeBlockItem> blockItems) {
        super(lineNum);
        this.blockItems = blockItems;
    }

    public ArrayList<NodeBlockItem> getBlockItems() {
        return blockItems;
    }
}
