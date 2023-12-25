package IRTools_llvm.Value.Instructions;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.User;

public class Instruction extends User {
    public Instruction(String name, Type type, BasicBlock parent) {
        super(name, type, parent);
    }
}
