package MIPSTools.Atom;

public class Label {
    private final String labelName;
    public Label(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        return this.labelName;
    }
}
