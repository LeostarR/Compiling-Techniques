package IRTools_llvm;

import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Function.Function;
import IRTools_llvm.Value.Instructions.Instruction;
import IRTools_llvm.Value.Instructions.Terminator.TermiBr;
import IRTools_llvm.Value.Instructions.Terminator.TermiCall;
import IRTools_llvm.Value.Module;
import IRTools_llvm.Value.User;
import IRTools_llvm.Value.Value;

import java.util.ArrayList;

public class MidOptimizer {
    private final Module module;
    public MidOptimizer(Module module, boolean button) {
        this.module = module;
        if (button) {
            this.optimize();
        }
    }

    private void optimize() {
        this.deleteFunction(module.getFunctions());
        for (Function function: module.getFunctions()) {
            this.inLineBlock(function);
        }
        for (Function function: module.getFunctions()) {
            this.deleteBlock(function);
        }
        this.mergeBlock();
        this.deleteCode();
    }

    //删除从未使用过的函数
    private void deleteFunction(ArrayList<Function> list) {
        ArrayList<Function> deleteList = new ArrayList<>();
        for (Function function: list) {
            if (function.isReserved() || function.getName().equals("@main")) {
                continue;
            }
            ArrayList<User> users = function.getUserList();
            if (users.isEmpty()) {  //无用函数，删除
                deleteList.add(function);
                //维护数据结构
                for (BasicBlock basicBlock: function.getBasicBlocks()) {
                    //找到所有的call指令，将call调用的函数和这条指令关系解除
                    //作为user,删除它与自己value的关系
                    for (Instruction instruction: basicBlock.getInstructions()) {
                        if (instruction instanceof TermiCall) {
                            Value value = instruction.getValue(0);
                            instruction.removeValue(value);
                        }
                    }
                }
            }
        }
        for (Function function: deleteList) {
            module.removeFunction(function);
        }
        if (!deleteList.isEmpty()) {
            this.deleteFunction(module.getFunctions());
        }
    }

    //内联基本块
    private void inLineBlock(Function function) {
        for (BasicBlock basicBlock: function.getBasicBlocks()) {
            if (basicBlock.getInstructions().size() == 1) {
                //只有一条无条件跳转语句
                Instruction instruction = basicBlock.getInstructions().get(0);
                if (instruction instanceof TermiBr
                        && !((TermiBr)instruction).isCondition()
                        && instruction.getValue(0) instanceof BasicBlock) {
                    ArrayList<User> users = new ArrayList<>(basicBlock.getUserList());
                    BasicBlock block = (BasicBlock) instruction.getValue(0);
                    for (User user: users) {
                        if (user instanceof TermiBr) {
                            this.replaceBlock((TermiBr) user, basicBlock, block);
                        }
                    }
                }
            }
        }
    }

    //br调用midBlock，midBlock无条件跳转至endBlock，简化
    private void replaceBlock(TermiBr br, BasicBlock midBlock, BasicBlock endBlock) {
        br.replaceValue(midBlock, endBlock);
    }

    //删除不可到达的基本块
    private void deleteBlock(Function function) {
        ArrayList<BasicBlock> deteleList = new ArrayList<>();
        if (function.getBasicBlocks().size() == 1) {
            return;
        }
        for (BasicBlock basicBlock: function.getBasicBlocks()) {
            if (function.getBasicBlocks().indexOf(basicBlock) == 0) {
                continue;
            }
            ArrayList<User> users = basicBlock.getUserList();
            if (users.isEmpty()) {  //无用基本块,删除
                deteleList.add(basicBlock);
                //维护数据结构
                //作为value没有被任何user调用
                //作为user,删除它与自己value的关系,BasicBlock构造时就不添加Value,因此无操作

                //删除指令之间的value-use关系，因为他们会被删除
                /*
                for (Instruction instruction: basicBlock.getInstructions()) {
                    //作为value,删除和它的User之间的关系
                    ArrayList<User> userArrayList = new ArrayList<>(instruction.getUserList());
                    for (User user: userArrayList) {
                        user.removeValue(instruction);
                    }
                    //作为user,删除它与自己value的关系
                    ArrayList<Value> operands = new ArrayList<>(instruction.getOperand());
                    for (Value value: operands) {
                        instruction.removeValue(value);
                    }
                }
                */
            }
        }
        for (BasicBlock basicBlock: deteleList) {
            function.removeBasicBlock(basicBlock);
        }
        if (!deteleList.isEmpty()) {
            this.deleteBlock(function);
        }
    }

    //合并基本块
    private void mergeBlock() {

    }

    //删除死代码（无用代码）
    private void deleteCode() {

    }

    public Module getModule() {
        return module;
    }
}
