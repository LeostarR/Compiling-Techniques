package MIPSTools.MipsInstructions.OtherType;

import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class Mfhi extends MIPSInstruction {
    private final Reg rd;
    public Mfhi(Reg rd) {
        this.rd = rd;
    }

    @Override
    public String toString() {
        return "mfhi " + this.rd.getName();
    }
}
