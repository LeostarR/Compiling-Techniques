package IRTools_llvm.Value.Instructions.Terminator;

import IRTools_llvm.Type.TypeVoid;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class TermiBr extends InstructionOfTerminator {
    private final boolean isCondition;

    public TermiBr(BasicBlock block, BasicBlock parent) {
        super("", new TypeVoid(), parent);
        this.addValue(block);
        this.isCondition = false;
    }

    public TermiBr(Value condition, BasicBlock b1, BasicBlock b2, BasicBlock parent) {
        super("", new TypeVoid(), parent);
        this.addValue(condition);
        this.addValue(b1);
        this.addValue(b2);
        isCondition = true;
    }

    public boolean isCondition() {
        return this.isCondition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.isCondition) {
            sb.append("br ").append(this.getValue(0).getType()).append(" %").append(this.getValue(0).getName());
        } else {
            sb.append("br ").append(this.getValue(0).getType()).append(" ").append(this.getValue(0).getName());
            sb.append(", ").append(this.getValue(1).getType()).append(" %").append(this.getValue(1).getName());
            sb.append(", ").append(this.getValue(2).getType()).append(" %").append(this.getValue(2).getName());
        }
        return sb.toString();
    }
}
