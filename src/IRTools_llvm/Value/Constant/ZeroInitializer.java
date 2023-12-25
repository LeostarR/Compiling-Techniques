package IRTools_llvm.Value.Constant;

import IRTools_llvm.Type.TypeArray;

public class ZeroInitializer extends Const {
    private final int numOfElement;
    public ZeroInitializer(TypeArray arrayType) {
        super("zeroinitializer", arrayType, null);
        numOfElement = arrayType.getNumOfElement();
    }

    public int getNumOfElement() {
        return numOfElement;
    }

    @Override
    public String toString() {
        return "zeroinitializer";
    }
}
