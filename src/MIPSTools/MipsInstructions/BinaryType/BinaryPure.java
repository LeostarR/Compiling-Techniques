package MIPSTools.MipsInstructions.BinaryType;

import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class BinaryPure extends MIPSInstruction {
    private final BinaryPureType opType;
    private final Reg rd;
    private final Reg rs;
    private final Reg rt;
    public BinaryPure(BinaryPureType opType, Reg rd, Reg rs, Reg rt) {
        this.opType = opType;
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return opType.toString() + " " + rd.getName() + ", " + rs.getName() + ", " + rt.getName();
    }
}
