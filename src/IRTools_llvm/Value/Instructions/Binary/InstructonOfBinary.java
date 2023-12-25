package IRTools_llvm.Value.Instructions.Binary;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Instructions.Instruction;

public class InstructonOfBinary extends Instruction {
    public InstructonOfBinary(String name, Type type, BasicBlock parent) {
        super("%v" + name, type, parent);
    }
}
