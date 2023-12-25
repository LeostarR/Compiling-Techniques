package IRTools_llvm.Value.Instructions.Memory;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class MemZextTo extends InstructionOfMemory {
    public MemZextTo(String name, Type toType, Value zextVal, BasicBlock parent) {
        super("%v" + name, toType, parent);
        this.addValue(zextVal);
    }

    @Override
    public String toString() {
        return this.getName() +
                " = zext " +
                this.getValue(0).getType() +
                " " +
                this.getValue(0).getName() +
                " to " +
                this.getType();
    }
}
