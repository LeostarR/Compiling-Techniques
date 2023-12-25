package IRTools_llvm.Type;

public class TypeInt extends Type{
    private final int bits;//bits->位数，例如32位整数

    public TypeInt(int valueInt) {
        this.bits = valueInt;
    }

    public int getBits() {
        return this.bits;
    }

    @Override
    public int getSpace() {
        return 4;
    }

    @Override
    public String toString() {
        return "i" + bits;
    }
}
