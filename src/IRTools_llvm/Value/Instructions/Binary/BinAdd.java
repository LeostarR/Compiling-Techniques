package IRTools_llvm.Value.Instructions.Binary;


import IRTools_llvm.Type.TypeInt;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class BinAdd extends InstructonOfBinary {
    public BinAdd(String name, Value v1, Value v2, BasicBlock parent) {
        super(name, new TypeInt(32), parent);
        this.addValue(v1);
        this.addValue(v2);
    }

    @Override
    public String toString() {
        return this.getName() +
                " = add " +
                this.getType() +
                " " +
                this.getValue(0).getName() +
                ", " +
                this.getValue(1).getName();
    }
}
