package MIPSTools.Tools;

import IRTools_llvm.Type.TypeAddr;
import IRTools_llvm.Value.Function.Argument;
import IRTools_llvm.Value.Function.Function;

import java.util.ArrayList;
import java.util.HashMap;

public class MIPSFunction {
    private int fp = 0;
    private final String funcName;
    private final Function function;
    private final ArrayList<MIPSBlock> blocks = new ArrayList<>();
    private final HashMap<String, Integer> varNames = new HashMap<>();
    private final ArrayList<String> indexArray = new ArrayList<>();
    private final HashMap<String, Integer> addrs = new HashMap<>();
    public MIPSFunction(String funcName, Function function) {
        this.funcName = funcName;
        this.function = function;
        for (Argument argument : this.function.getArguments()) {
            this.varNames.put(argument.getName().substring(1), this.fp);
            if (argument.getType() instanceof TypeAddr) {
                this.indexArray.add(argument.getName().substring(1));
            }
            this.fp += 4;
        }
    }

    /***************************setter***************************/

    public void addFp(int offset) {
        this.fp += offset;
    }

    public void addBlock(MIPSBlock block) {
        this.blocks.add(block);
    }

    public void addVarName(String varName, int offset) {
        this.varNames.put(varName, this.fp);
        this.fp += offset;
    }

    public void addIndex(String varName) {
        this.indexArray.add(varName);
    }

    public void addAddrs(String addr, int offset) {
        this.addrs.put(addr, this.fp);
        this.fp += offset;
    }

    /***************************getter***************************/

    public int getFp() {
        return this.fp;
    }

    public String getFuncName() {
        return this.funcName;
    }

    public Function getFunction() {
        return this.function;
    }

    public ArrayList<MIPSBlock> getBlocks() {
        return this.blocks;
    }

    public HashMap<String, Integer> getVarNames() {
        return this.varNames;
    }

    public ArrayList<String> getIndexArray() {
        return this.indexArray;
    }

    public HashMap<String, Integer> getAddrs() {
        return this.addrs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!function.isReserved()) {
            sb.append(this.funcName);
            sb.append(":\n");
            for (MIPSBlock block : blocks) {
                sb.append(block.toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
