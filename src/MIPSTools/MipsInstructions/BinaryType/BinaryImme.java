package MIPSTools.MipsInstructions.BinaryType;

import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class BinaryImme extends MIPSInstruction {
    private final BinaryImmeType binaryImmeType;
    private final Reg rt;
    private final Reg rs;
    private final Imme imme;
    public BinaryImme(BinaryImmeType binaryImmeType, Reg rt, Reg rs, Imme imme) {
        this.binaryImmeType = binaryImmeType;
        this.rt = rt;
        this.rs = rs;
        this.imme = imme;
    }

    @Override
    public String toString() {
        return binaryImmeType.toString() + " " + rt.getName() + ", " + rs.getName() + ", " + imme.getDecName();
    }
}
