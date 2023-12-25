package IRTools_llvm.Value;

import IRTools_llvm.Value.Function.Function;
import IRTools_llvm.Value.Constant.GlobalVar;

import java.util.ArrayList;
import java.util.Objects;

public class Module extends Value {
    private static final Module module = new Module();
    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();

    public Module() {
        super("module", null, null) ;
    }

    public static Module getInstance() {
        return module;
    }

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVars.add(globalVar);
    }

    public void addFunction(Function function) {
        this.functions.add(function);
    }

    public void removeFunction(Function function) {
        this.functions.remove(function);
    }

    public ArrayList<GlobalVar> getGlobalVars() {
        return globalVars;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public Function getFunctionByName(String name) {
        for (Function function: functions) {
            if (Objects.equals(function.getName(), name)) {
                return function;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Function function: functions) {
            if (function.isReserved()) {
                sb.append(function).append("\n");
            }
        }
        for (GlobalVar globalVar: globalVars) {
            sb.append(globalVar.toString()).append("\n");
        }
        for (Function function: functions) {
            if (!function.isReserved()) {
                sb.append(function).append("\n");
            }
        }
        return sb.toString();
    }
}
