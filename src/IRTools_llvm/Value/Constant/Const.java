package IRTools_llvm.Value.Constant;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Value.User;
import IRTools_llvm.Value.Value;

public abstract class Const extends User {
    public Const(String name, Type type, Value parent) {
        super(name, type, parent);
    }
}
