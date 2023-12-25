package IRTools_llvm.Value.Instructions.Terminator;

import IRTools_llvm.Type.TypeVoid;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class TermiRet extends InstructionOfTerminator {
    private final boolean isVoid;

    public TermiRet(Value retValue, BasicBlock parent) {
        super("", new TypeVoid(), parent);
        this.addValue(retValue);
        this.isVoid = false;
    }

    public TermiRet(BasicBlock parent) {
        super("", new TypeVoid(), parent);
        this.isVoid = true;
    }

    public boolean isVoid() {
        return isVoid;
    }

    @Override
    public String toString() {
        if (!this.isVoid) {
            return "ret " +
                    this.getValue(0).getType() +
                    " " +
                    this.getValue(0).getName();
        } else {
            return "ret void";
        }
    }
}
