package IRTools_llvm.Value.Instructions.Memory;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class MemLoad extends InstructionOfMemory {
    private final Type type;

    public MemLoad(String name, Type type, Value addr, BasicBlock parent) {
        super("%v" + name, type, parent);
        this.addValue(addr);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.getName() +
                " = load " +
                this.getType() +
                ", " +
                this.getValue(0).getType() +
                " " +
                this.getValue(0).getName();
    }
}
