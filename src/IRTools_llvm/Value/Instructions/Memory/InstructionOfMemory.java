package IRTools_llvm.Value.Instructions.Memory;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Instructions.Instruction;

public class InstructionOfMemory extends Instruction {
    public InstructionOfMemory(String name, Type type, BasicBlock parent) {
        super(name, type, parent);
    }
}
