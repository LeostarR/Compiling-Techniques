package IRTools_llvm.Type;

public class TypeArray extends Type {
    private final int numOfElement;
    private final Type typeOfElement;

    public TypeArray(int numOfElement, Type typeOfElement) {    //numOfElement->数组一维空间的size，即多少个元素, typeOfElement->每一个元素的type（应相同）
        this.numOfElement = numOfElement;
        this.typeOfElement = typeOfElement;
    }

    public int getNumOfElement() {
        return this.numOfElement;
    }

    public Type getTypeOfElement() {
        return this.typeOfElement;
    }

    @Override
    public int getSpace() {
        return this.numOfElement * this.typeOfElement.getSpace();
    }

    @Override
    public String toString() {
        return "[" + this.numOfElement + " x " + this.typeOfElement.toString() + "]";
    }
}
