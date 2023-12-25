package IRTools_llvm.Type;

import java.util.ArrayList;

public class TypeFunction extends Type {
    private final ArrayList<Type> params;//参数的type集合
    private final Type returnType;      //返回值的type

    public TypeFunction(ArrayList<Type> params, Type returnType) {
        this.params = params;
        this.returnType = returnType;
    }

    public ArrayList<Type> getParams() {
        return this.params;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    @Override
    public int getSpace() {
        return 0;
    }
}
