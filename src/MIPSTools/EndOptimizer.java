package MIPSTools;

import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Label;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.MIPSInstruction;
import MIPSTools.MipsInstructions.OtherType.La;
import MIPSTools.MipsInstructions.OtherType.Li;
import MIPSTools.MipsInstructions.OtherType.Syscall;
import MIPSTools.Tools.MIPSBlock;
import MIPSTools.Tools.MIPSFunction;
import MIPSTools.Tools.MIPSModule;

import java.util.ArrayList;

public class EndOptimizer {
    private final MIPSModule mipsModule;
    public EndOptimizer(MIPSModule mipsModule, boolean button) {
        this.mipsModule = mipsModule;
        if (button) {
            this.optimize();
        }
    }

    private void optimize() {
        this.modifyStr();
    }

    private void modifyStr() {
        ArrayList<String> list = new ArrayList<>();
        for (MIPSFunction function: mipsModule.getFunctions()) {
            for (MIPSBlock block: function.getBlocks()) {
                ArrayList<MIPSInstruction> instructions = new ArrayList<>(block.getInstructions());
                int begin = -1;
                int end = 0;
                StringBuilder sb = new StringBuilder();
                ArrayList<Object[]> delete = new ArrayList<>();
                for (int i = 0;i < instructions.size() - 3;i++) {
                    MIPSInstruction inst1 = instructions.get(i);
                    MIPSInstruction inst2 = instructions.get(i + 1);
                    MIPSInstruction inst3 = instructions.get(i + 2);
                    if (inst1.toString().equals("li $v0, 11")
                            && inst2 instanceof Li && ((Li) inst2).getR().getName().equals("$a0")
                            && inst3 instanceof Syscall) {
                        if (((Li) inst2).getImme().getValue() == 10) {
                            sb.append("\\n");
                        } else {
                            sb.append((char)((Li) inst2).getImme().getValue());
                        }
                        if (begin < 0) {
                            begin = i;
                        }
                        if (i + 2 >= end) {
                            end = i + 2;
                        }
                        i += 2;
                    } else {
                        if (sb.toString().isEmpty()) {
                            continue;
                        }
                        if (!list.contains(sb.toString())) {
                            list.add(sb.toString());
                        }
                        delete.add(new Object[]{begin, end, sb.toString()});
                        begin = -1;
                        end = 0;
                        sb = new StringBuilder();
                    }
                }
                if (begin != -1 && end != 0 && !sb.toString().isEmpty()) {
                    if (!list.contains(sb.toString())) {
                        list.add(sb.toString());
                    }
                    delete.add(new Object[]{begin, end, sb.toString()});
                }
                for (int i = delete.size() - 1;i >= 0;i--) {
                    begin = (int) delete.get(i)[0];
                    end = (int) delete.get(i)[1];
                    String str = (String) delete.get(i)[2];
                    Li ins1 = new Li(new Reg(2), new Imme(4));
                    La ins2 = new La(new Reg(4), new Label("str" + list.indexOf(str)));
                    Syscall ins3 = new Syscall();
                    while (begin <= end) {
                        block.getInstructions().remove(end--);
                    }
                    block.getInstructions().add(begin++, ins1);
                    block.getInstructions().add(begin++, ins2);
                    block.getInstructions().add(begin++, ins3);
                }
            }
        }
        mipsModule.getStrings().addAll(list);
    }

    public MIPSModule getMipsModule() {
        return this.mipsModule;
    }
}
