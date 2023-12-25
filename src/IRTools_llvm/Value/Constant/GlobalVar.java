package IRTools_llvm.Value.Constant;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Type.TypeAddr;
import IRTools_llvm.Type.TypeArray;
import IRTools_llvm.Type.TypeInt;
import IRTools_llvm.Value.Value;
import IRTools_llvm.Value.Module;

public class GlobalVar extends Const {
    private final boolean isConst;
    private final boolean init;
    public GlobalVar(String name, Type type) { //没初值一定不是常量
        super("@" + name, new TypeAddr(type), Module.getInstance());
        this.isConst = false;
        this.init = false;
        this.addValue(this.getZeroConst(type));
    }

    public GlobalVar(String name, boolean isConst, Const init) {
        super("@" + name, new TypeAddr(init.getType()), Module.getInstance());
        this.isConst = isConst;
        this.init = true;
        this.addValue(init);

    }

    public Value getZeroConst(Type type) {
        if (type instanceof TypeInt) {
            return new ConstInt(32, 0);
        } else {
            return new ConstArray((TypeArray) type);
        }
    }

    public boolean isConst() {
        return this.isConst;
    }

    public boolean isInit() {
        return this.init;
    }

    public Const getVal() {
        return ((Const) this.getValue(0));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String s;
        if (this.isConst) {
            s = "constant";
        } else {
            s = "global";
        }
        sb.append(this.getName()).append(" = dso_local ");
        sb.append(s).append(" ");
        sb.append(((TypeAddr) this.getType()).getSrcType().toString());
        sb.append(" ").append(this.getValue(0).toString());
        return sb.toString();
    }
}
