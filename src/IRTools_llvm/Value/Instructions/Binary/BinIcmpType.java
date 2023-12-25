package IRTools_llvm.Value.Instructions.Binary;

public enum BinIcmpType {
    EQ,
    NE,
    SGT,    //signed greater than
    SGE,    //signed greater or equal
    SLT,    //signed less than
    SLE;    //signed less or equal

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
