package MIPSTools.MipsInstructions.JType;

import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class Jr extends MIPSInstruction {
    private final Reg reg;
    public Jr(Reg reg) {
        this.reg = reg;
    }

    @Override
    public String toString() {
        return "jr " + this.reg.getName();
    }
}
