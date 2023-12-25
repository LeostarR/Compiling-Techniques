package MIPSTools.MipsInstructions.OtherType;

import MIPSTools.Atom.Label;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class La extends MIPSInstruction {
    private final Reg r;
    private final Label label;
    public La(Reg r, Label label) {
        this.r = r;
        this.label = label;
    }

    @Override
    public String toString() {
        return "la " + this.r.getName() + ", " + this.label.toString();
    }
}
