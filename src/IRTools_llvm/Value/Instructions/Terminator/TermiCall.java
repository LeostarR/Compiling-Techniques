package IRTools_llvm.Value.Instructions.Terminator;

import IRTools_llvm.Type.TypeVoid;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Function.Function;
import IRTools_llvm.Value.Value;

import java.util.ArrayList;

public class TermiCall extends InstructionOfTerminator {
    private final boolean isVoid;

    public TermiCall(Function function, ArrayList<Value> args, BasicBlock parent) {
        super("", new TypeVoid(), parent);
        this.addValue(function);
        for (Value value: args) {
            this.addValue(value);
        }
        this.isVoid = true;
    }

    public TermiCall(String name, Function function, ArrayList<Value> args, BasicBlock parent) {
        super("%v" + name, function.getReturnType(), parent);
        this.addValue(function);
        for (Value value: args) {
            this.addValue(value);
        }
        isVoid = false;
    }

    public boolean isVoid() {
        return isVoid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isVoid) {
            sb.append("call ");
        } else {
            sb.append(this.getName()).append(" = call ");
        }
        sb.append(this.getType()).append(" ");;
        sb.append(this.getValue(0).getName());
        sb.append("(");
        int argsNum = ((Function) this.getValue(0)).getArguments().size();
        for (int i = 1; i <= argsNum; i++) {
            if (i < this.getNumofValues()) {
                sb.append(this.getValue(i).getType()).append(" ");
                sb.append(this.getValue(i).getName()).append(", ");
            }

        }
        if (!((Function) this.getValue(0)).getArguments().isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }
}
