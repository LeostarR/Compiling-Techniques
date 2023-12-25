package IRTools_llvm.Value.Instructions.Memory;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Type.TypeAddr;
import IRTools_llvm.Type.TypeArray;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Value;

public class MemGetelementptr extends InstructionOfMemory{
    private final Type type;

    public MemGetelementptr(String name, TypeArray type, Value base, Value pointIndex, BasicBlock parent) {
        super("%v" + name, new TypeAddr(type), parent);
        this.addValue(base);
        this.addValue(pointIndex);
        this.type = type;
    }

    public MemGetelementptr(String name, TypeAddr type, Value base, Value pointIndex, BasicBlock parent) {
        super("%v" + name, type, parent);
        this.addValue(base);
        this.addValue(pointIndex);
        this.type = type.getSrcType();
    }

    public MemGetelementptr(String name, TypeArray type, Value base, Value pointIndex1, Value pointIndex2, BasicBlock parent) {
        super("%v" + name, new TypeAddr(type.getTypeOfElement()), parent);
        this.addValue(base);
        this.addValue(pointIndex1);
        this.addValue(pointIndex2);
        this.type = type;
    }

    public MemGetelementptr(String name, TypeAddr type, Value base, Value pointIndex1, Value pointIndex2, BasicBlock parent) {
        super("%v" + name, new TypeAddr(((TypeArray) type.getSrcType()).getTypeOfElement()), parent);
        this.addValue(base);
        this.addValue(pointIndex1);
        this.addValue(pointIndex2);
        this.type = type.getSrcType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = getelementptr ");
        sb.append(type).append(", ");
        for (Value operand: this.getOperand()) {
            sb.append(operand.getType()).append(" ");
            sb.append(operand.getName()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
