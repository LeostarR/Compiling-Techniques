package MIPSTools.MipsInstructions.BinaryType;

import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class BinaryDiv extends MIPSInstruction {    //符号除
    private final Reg rs;
    private final Reg rt;
    public BinaryDiv (Reg rs, Reg rt) {
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return "div " + rs.getName() + ", " + rt.getName();
    }
}
