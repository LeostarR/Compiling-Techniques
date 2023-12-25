package IRTools_llvm.Type;

public class TypeAddr extends Type {    //指针或者数组地址， 相当于嵌套了一层type
    private final Type type;    //源类型，例如数组是整型/浮点（作业只要求整型）

    public TypeAddr(Type type) {
        this.type = type;
    }

    public Type getSrcType() {
        return this.type;
    }

    @Override
    public int getSpace() {
        return 4;
    }

    @Override
    public String toString() {
        return this.type.toString() + "*";
    }
}
