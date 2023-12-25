package MIPSTools.MipsInstructions.JType;

import MIPSTools.Atom.Label;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class J extends MIPSInstruction {
    private final Label label;
    public J (Label label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "j " + this.label.toString();
    }
}
