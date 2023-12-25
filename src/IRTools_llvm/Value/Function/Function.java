package IRTools_llvm.Value.Function;

import IRTools_llvm.Type.*;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Constant.Const;
import IRTools_llvm.Value.Module;

import java.util.*;

public class Function extends Const {
    private final boolean isReserved;
    private static final HashMap<String, Function> reservedFunctions = new HashMap<>();

    static
    {
        reservedFunctions.put("getint", new Function("getint", new TypeFunction(new ArrayList<>(), new TypeInt(32)), true));
        reservedFunctions.put("putint", new Function("putint", new TypeFunction(new ArrayList<>(Arrays.asList(new TypeInt(32))), new TypeVoid()), true));
        reservedFunctions.put("putch", new Function("putch", new TypeFunction(new ArrayList<>(Arrays.asList(new TypeInt(32))), new TypeVoid()), true));
        reservedFunctions.put("putstr", new Function("putstr", new TypeFunction(new ArrayList<>(Arrays.asList(new TypeAddr(new TypeInt(8)))), new TypeVoid()), true));
        for (Function function : reservedFunctions.values()) {
            Module.getInstance().addFunction(function);
        }
    };
    private final ArrayList<Argument> arguments = new ArrayList<>();
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    public PriorityQueue<BasicBlock> pq = new PriorityQueue<>();

    public Function(String name, TypeFunction functionType, boolean isReserved) {
        super("@" + name, functionType, Module.getInstance());
        this.isReserved = isReserved;
        this.addValue(Module.getInstance());
        int argCnt = 0;
        for (Type type : ((TypeFunction) this.getType()).getParams()) {
            Argument argument = new Argument("v" + argCnt++, type, this);
            this.arguments.add(argument);
        }
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        this.basicBlocks.add(basicBlock);
    }

    public void removeBasicBlock(BasicBlock basicBlock) {
        this.basicBlocks.remove(basicBlock);
    }

    public Type getReturnType() {
        return ((TypeFunction) this.getType()).getReturnType();
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }


    public String  toString() {
        StringBuilder sb = new StringBuilder();
        String s;
        if (this.isReserved()) {
            s = "declare ";
        } else {
            s = "\ndefine dso_local ";
        }
        sb.append(s);
        sb.append(((TypeFunction) this.getType()).getReturnType().toString());
        sb.append(" ").append(this.getName()).append("(");
        for (Argument argument : this.arguments) {
            sb.append(argument.getType()).append(" ");
            if (!isReserved()) {
                sb.append(argument.getName()).append(", ");
            }
        }
        if (!this.arguments.isEmpty()) {
            if (this.isReserved()) {
                sb.delete(sb.length() - 1, sb.length());
            } else {
                sb.delete(sb.length() - 2, sb.length());
            }
        }
        sb.append(")");
        if (!isReserved()) {
            sb.append("{\n");
            basicBlocks.sort(Comparator.comparingInt(BasicBlock::getMinOrder));
            for (BasicBlock block: basicBlocks) {
                sb.append(block.toString()).append("\n");
            }
            if (!basicBlocks.isEmpty()) {
                sb.delete(sb.length() - 1, sb.length());
            }
            sb.append("}");
        }
        return sb.toString();
    }
}
