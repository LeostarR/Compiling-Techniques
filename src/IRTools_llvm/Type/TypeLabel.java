package IRTools_llvm.Type;

public class TypeLabel extends Type {
    @Override
    public int getSpace() {
        return 0;
    }

    @Override
    public String toString() {
        return "label";
    }
}
