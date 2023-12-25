package IRTools_llvm.Value.Instructions.Terminator;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Instructions.Instruction;

public class InstructionOfTerminator extends Instruction {
    public InstructionOfTerminator(String name, Type type, BasicBlock parent) {
        super(name, type, parent);
    }
}
