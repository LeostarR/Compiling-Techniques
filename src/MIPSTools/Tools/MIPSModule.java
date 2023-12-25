package MIPSTools.Tools;

import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Label;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.JType.J;
import MIPSTools.MipsInstructions.OtherType.Li;

import java.util.ArrayList;

public class MIPSModule {
    private final ArrayList<MIPSGlobalVar> globalVars = new ArrayList<>();
    private ArrayList<String> strings = new ArrayList<>();
    private final ArrayList<MIPSFunction> functions = new ArrayList<>();
    public MIPSModule() {
    }

    private static final MIPSModule mipsModule = new MIPSModule();
    public static MIPSModule getInstance() {
        return mipsModule;
    }

    public void addGlobalVar(MIPSGlobalVar globalVar) {
        this.globalVars.add(globalVar);
    }

    public void addFunction(MIPSFunction function) {
        this.functions.add(function);
    }

    public ArrayList<MIPSGlobalVar> getGlobalVars() {
        return globalVars;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    public ArrayList<MIPSFunction> getFunctions() {
        return functions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.globalVars.isEmpty() || !this.strings.isEmpty()) {
            sb.append(".data\n");
            for (MIPSGlobalVar globalVar : this.globalVars) {
                sb.append(globalVar.toString());
            }
            for (int i = 0;i < strings.size();i++) {
                sb.append("str").append(i);
                sb.append(": .asciiz ");
                sb.append("\"").append(strings.get(i)).append("\"");
                sb.append("\n");
            }
        }
        sb.append("\n");
        sb.append(".text");
        sb.append("\n");
        sb.append(new Li(new Reg(30), new Imme(0x10040000)));
        sb.append("\n");
        sb.append(new J(new Label("main")));
        sb.append("\n").append("\n");
        for (MIPSFunction function : this.functions) {
            sb.append(function.toString());
        }
        return sb.toString();
    }
}
