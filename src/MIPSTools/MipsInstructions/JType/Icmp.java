package MIPSTools.MipsInstructions.JType;

import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class Icmp extends MIPSInstruction {
    private final IcmpType icmpType;
    private final Reg rd;
    private final Reg rs;
    private final Reg rt;
    public Icmp(IcmpType icmpType, Reg rd, Reg rs, Reg rt) {
        this.icmpType = icmpType;
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return this.icmpType.toString() + " " + this.rd.getName() + ", " + this.rs.getName() + ", "  + this.rt.getName();
    }
}
