package MIPSTools.Atom;

public class Imme {
    private final int value;
    public Imme(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getDecName() {
        return this.toString();
    }

    public String getHexName() {
        return "0x" + Integer.toHexString(this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
