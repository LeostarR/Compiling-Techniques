package IRTools_llvm.Type;

public class TypeVoid extends Type{
    @Override
    public int getSpace() {
        return 0;
    }

    @Override
    public String toString() {
        return "void";
    }
}
