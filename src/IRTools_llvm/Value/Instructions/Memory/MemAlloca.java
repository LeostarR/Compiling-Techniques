package IRTools_llvm.Value.Instructions.Memory;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Type.TypeAddr;
import IRTools_llvm.Value.BasicBlock.BasicBlock;

public class MemAlloca extends InstructionOfMemory {
    public MemAlloca(String name, Type allocatedType, BasicBlock parent) {
        super("%v" + name, new TypeAddr(allocatedType), parent);
    }

    @Override
    public String toString() {
        return this.getName() +
                " = alloca " +
                ((TypeAddr) this.getType()).getSrcType(); //输出为源类型而不是指针类型
    }
}
