package MIPSTools.Atom;

public class Reg {
    private final int id;
    public Reg(int id) {
        this.id = id;
    }

    public String getName() {
        if (this.id == 0) {
            return "$zero";
        } else if (this.id == 1) {
            return "$at";
        } else if (this.id >= 2 && this.id <= 3) {
            return "$v" + (this.id - 2);
        } else if (this.id >= 4 && this.id <= 7) {
            return "$a" + (this.id - 4);
        } else if (this.id >= 8 && this.id <= 15) {
            return "$t" + (this.id - 8);
        } else if (this.id >=16 && this.id <= 23) {
            return "$s" + (this.id - 16);
        } else if (this.id >= 24 && this.id <= 25) {
            return "$t" + (this.id - 24 + 8);
        } else if (this.id == 28) {
            return "$gp";
        } else if (this.id == 29) {
            return "$sp";
        } else if (this.id == 30) {
            return "$fp";
        } else if (this.id == 31) {
            return "$ra";
        } else {
            return this.toString();
        }
    }

    @Override
    public String toString() {
        return "$" + this.id;
    }
}
