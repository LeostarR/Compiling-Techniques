package MIPSTools.MipsInstructions.BinaryType;

import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class BinaryMulu extends MIPSInstruction {   //符号乘
    private final Reg rs;
    private final Reg rt;
    private final Reg rd;
    private final Imme imme;
    public BinaryMulu(Reg rs, Reg rt, Reg rd) {
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
        this.imme = null;
    }

    public BinaryMulu(Reg rs, Reg rt, Imme imme) {
        this.rs = rs;
        this.rt = rt;
        this.rd = null;
        this.imme = imme;
    }

    @Override
    public String toString() {
        if (this.rd != null) {
            return "mulu " + rs.getName() + ", " + rt.getName() + ", " + rd.getName();
        } else if (this.imme != null) {
            return "mulu " + rs.getName() + ", " + rt.getName() + ", " + imme.getDecName();
        }
        return null;
    }
}
