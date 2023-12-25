package IRTools_llvm.Value.Constant;


import IRTools_llvm.Type.TypeInt;

public class ConstInt extends Const {
    private final int val;
    public ConstInt(int bits, int val) {
        super(Integer.toString(val), new TypeInt(bits), null);
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
