package MIPSTools.MipsInstructions.JType;

import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Label;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class Beq extends MIPSInstruction {
    private final Reg rs;
    private final Imme imme;
    private final Label label;
    public Beq(Reg rs, Imme imme, Label label) {
        this.rs = rs;
        this.imme = imme;
        this.label = label;
    }

    @Override
    public String toString() {
        return "beq " + this.rs.getName() + ", " + this.imme.getDecName() + ", " + this.label.toString();
    }
}
