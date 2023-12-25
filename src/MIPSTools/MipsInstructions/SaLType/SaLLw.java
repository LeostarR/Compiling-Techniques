package MIPSTools.MipsInstructions.SaLType;

import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Label;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class SaLLw extends MIPSInstruction {
    private final Reg base;
    private final Reg rt;
    private final Imme offset;
    private final Label label;
    public SaLLw(Reg base, Reg rt, Imme offset) {
        this.base = base;
        this.rt = rt;
        this.offset = offset;
        this.label = null;
    }

    public SaLLw(Reg rt, Label label) {
        this.base = null;
        this.rt = rt;
        this.offset = null;
        this.label = label;
    }

    @Override
    public String toString() {
        if (this.offset != null && this.base != null) {
            return "lw " + this.rt.getName() + ", " + this.offset.getDecName() + "(" + this.base.getName() + ")";
        } else {
            return "lw " + this.rt.getName() + ", " + this.label;
        }
    }
}
