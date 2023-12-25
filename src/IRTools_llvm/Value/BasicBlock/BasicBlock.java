package IRTools_llvm.Value.BasicBlock;

import IRTools_llvm.Type.TypeLabel;
import IRTools_llvm.Value.Instructions.Instruction;
import IRTools_llvm.Value.Value;

import java.util.ArrayList;

public class BasicBlock extends Value implements Comparable<BasicBlock> {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    public BasicBlock(String name, Value parent) {
        super(name, new TypeLabel(), parent);
    }

    public void addInstruction(Instruction instruction) {
        this.instructions.add(instruction);
    }

    public ArrayList<Instruction> getInstructions() {
        return this.instructions;
    }

    public int getMinOrder() {
        if (this.getName().substring(this.getName().length() - 2).equals("_0")) {
            return 0;
        }
        for (Instruction instruction: instructions) {
            if (!instruction.getName().isEmpty() && instruction.getName().substring(2).matches("\\d+")) {
                return Integer.parseInt(instruction.getName().substring(2));
            }
        }
        /*
        for (Instruction instruction: instructions) {
            for (Value value: instruction.getOperand()) {
                if (!value.getName().isEmpty() && value.getName().substring(2).matches("\\d+")) {
                    return Integer.parseInt(value.getName().substring(2));
                }
            }
        }
        */
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(":\n");
        for (Instruction instruction : instructions) {
            sb.append("\t").append(instruction.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(BasicBlock o) {
        return Integer.compare(this.getMinOrder(), o.getMinOrder());
    }
}
