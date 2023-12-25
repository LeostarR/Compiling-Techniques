package IRTools_llvm.Value.Instructions.Binary;

import IRTools_llvm.Type.TypeInt;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class BinIcmp extends InstructonOfBinary{
    private final BinIcmpType icmpType;

    public BinIcmp(String name, BinIcmpType icmpType, Value v1, Value v2, BasicBlock parent) {
        super(name, new TypeInt(1), parent);
        this.addValue(v1);
        this.addValue(v2);
        this.icmpType = icmpType;
    }

    public BinIcmpType getIcmpType() {
        return this.icmpType;
    }

    @Override
    public String toString() {
        return this.getName()
                + " = icmp " +
                this.icmpType.toString() +
                " " +
                this.getValue(0).getType() +
                " " +
                this.getValue(0).getName() +
                ", "  +
                this.getValue(1).getName();
    }
}
