package MIPSTools.MipsInstructions.JType;

import MIPSTools.Atom.Label;
import MIPSTools.MipsInstructions.MIPSInstruction;

public class Jal extends MIPSInstruction {
    private final Label label;
    public Jal(Label label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "jal " + this.label.toString();
    }
}
