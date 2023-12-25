package IRTools_llvm.Value.Function;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.Value;

public class Argument extends Value {   //函数形参
    public Argument(String name, Type type, Value parent) {
        super("%" + name, type, parent);
    }
}
