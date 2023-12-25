package MIPSTools.MipsInstructions.OtherType;

import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class Mflo extends MIPSInstruction {
    private final Reg rd;
    public Mflo(Reg rd) {
        this.rd = rd;
    }

    @Override
    public String toString() {
        return "mflo " + this.rd.getName();
    }
}
