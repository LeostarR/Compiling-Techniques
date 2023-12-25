package IRTools_llvm.Value.Instructions.Memory;

import IRTools_llvm.Type.TypeVoid;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class MemStore extends InstructionOfMemory {
    public MemStore(Value storeVal, Value addr, BasicBlock parent) {
        super("", new TypeVoid(), parent);
        this.addValue(storeVal);
        this.addValue(addr);
    }

    @Override
    public String toString() {
        return "store " +
                this.getValue(0).getType() +
                " " +
                this.getValue(0).getName() +
                ", " +
                this.getValue(1).getType() +
                " " +
                this.getValue(1).getName();
    }
}
