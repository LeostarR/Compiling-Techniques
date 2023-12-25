package MIPSTools.MipsInstructions.OtherType;

import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class Li extends MIPSInstruction {
    private final Reg r;
    private final Imme imme;
    public Li(Reg r, Imme imme) {
        this.r = r;
        this.imme = imme;
    }

    public Reg getR() {
        return this.r;
    }

    public Imme getImme() {
        return this.imme;
    }

    @Override
    public String toString() {
        return "li " + this.r.getName() + ", " + this.imme.getDecName();
    }
}
