package MIPSTools.Tools;

import IRTools_llvm.Value.BasicBlock.BasicBlock;
import MIPSTools.MipsInstructions.MIPSInstruction;

import java.util.ArrayList;

public class MIPSBlock {
    private final String blockName;
    private final BasicBlock basicBlock;
    private final MIPSFunction mipsFunction;
    private final ArrayList<MIPSInstruction> Instructions = new ArrayList<>();
    public MIPSBlock(String blockName, BasicBlock basicBlock, MIPSFunction mipsFunction) {
        this.blockName = blockName;
        this.basicBlock = basicBlock;
        this.mipsFunction = mipsFunction;
    }

    public void addInstruction(MIPSInstruction instruction) {
        this.Instructions.add(instruction);
    }

    public String getBlockName() {
        return this.blockName;
    }

    public BasicBlock getBasicBlock() {
        return this.basicBlock;
    }

    public MIPSFunction getParentMipsFunction() {
        return this.mipsFunction;
    }

    public ArrayList<MIPSInstruction> getInstructions() {
        return Instructions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.blockName).append(":\n");
        for (MIPSInstruction instruction : this.Instructions) {
            sb.append("\t");
            sb.append(instruction.toString());
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
