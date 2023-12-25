package MIPSTools.Tools;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Type.TypeAddr;
import IRTools_llvm.Type.TypeArray;
import IRTools_llvm.Type.TypeInt;
import IRTools_llvm.Value.Constant.ConstArray;
import IRTools_llvm.Value.Constant.ConstInt;
import IRTools_llvm.Value.Constant.GlobalVar;

public class MIPSGlobalVar {
    private final String varName;
    private final GlobalVar var;
    public MIPSGlobalVar(String varName, GlobalVar var) {
        this.varName = varName;
        this.var = var;
    }

    public String getVarName() {
        return varName;
    }

    public GlobalVar getVar() {
        return var;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.varName).append(":\t");
        Type type = ((TypeAddr) var.getType()).getSrcType();
        if (type instanceof TypeInt) {
            sb.append(".word").append("\t");
            sb.append(((ConstInt) var.getVal()).getVal());
        } else if (type instanceof TypeArray) {
            if (this.var.isInit()) {
                sb.append(".word").append("\t");
                for (Integer integer : ((ConstArray) var.getVal()).getVal()) {
                    sb.append(integer).append(", ");
                }
                if (!((ConstArray) var.getVal()).getVal().isEmpty()) {
                    sb.delete(sb.length() - 2, sb.length());
                }
            } else {
                sb.append(".space").append("\t");
                sb.append(type.getSpace());
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}
