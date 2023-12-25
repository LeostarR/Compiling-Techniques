package MIPSTools;

import IRTools_llvm.Type.TypeAddr;
import IRTools_llvm.Type.TypeArray;
import IRTools_llvm.Type.TypeInt;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Constant.ConstInt;
import IRTools_llvm.Value.Constant.GlobalVar;
import IRTools_llvm.Value.Function.Function;
import IRTools_llvm.Value.Instructions.Binary.*;
import IRTools_llvm.Value.Instructions.Instruction;
import IRTools_llvm.Value.Instructions.Memory.*;
import IRTools_llvm.Value.Instructions.Terminator.InstructionOfTerminator;
import IRTools_llvm.Value.Instructions.Terminator.TermiBr;
import IRTools_llvm.Value.Instructions.Terminator.TermiCall;
import IRTools_llvm.Value.Instructions.Terminator.TermiRet;
import IRTools_llvm.Value.Module;
import MIPSTools.Atom.Imme;
import MIPSTools.Atom.Label;
import MIPSTools.Atom.Reg;
import MIPSTools.MipsInstructions.BinaryType.*;
import MIPSTools.MipsInstructions.JType.*;
import MIPSTools.MipsInstructions.OtherType.*;
import MIPSTools.MipsInstructions.SaLType.SaLLw;
import MIPSTools.MipsInstructions.SaLType.SaLSw;
import MIPSTools.Tools.MIPSBlock;
import MIPSTools.Tools.MIPSFunction;
import MIPSTools.Tools.MIPSGlobalVar;
import MIPSTools.Tools.MIPSModule;

import java.util.Objects;

public class LLVMParser {
    private final Module module;
    private final MIPSModule mipsModule;

    public LLVMParser() {
        module = Module.getInstance();
        mipsModule = MIPSModule.getInstance();
    }

    public void visitModule() {
        for (GlobalVar globalVar: module.getGlobalVars()) {
            String varName = globalVar.getName().substring(1);
            MIPSGlobalVar mipsGlobalVar = new MIPSGlobalVar(varName, globalVar);
            mipsModule.addGlobalVar(mipsGlobalVar);
        }
        for (Function function: module.getFunctions()) {
            String funcName = function.getName().substring(1);
            MIPSFunction mipsFunction = new MIPSFunction(funcName, function);
            mipsModule.addFunction(mipsFunction);
            this.visitFunction(mipsFunction, function);
        }
    }

    public void visitFunction(MIPSFunction mipsFunction, Function function) {
        for (BasicBlock basicBlock: function.getBasicBlocks()) {
            String blockName = basicBlock.getName();//.substring(1);
            MIPSBlock mipsBlock = new MIPSBlock(blockName, basicBlock, mipsFunction);
            mipsFunction.addBlock(mipsBlock);
            this.visitBasicBlock(mipsBlock, basicBlock);
        }
    }

    public void visitBasicBlock(MIPSBlock mipsBlock, BasicBlock basicBlock) {
        MIPSFunction parentFunction = mipsBlock.getParentMipsFunction();
        for (Instruction instruction: basicBlock.getInstructions()) {
            if (instruction instanceof InstructionOfMemory) {
                if (instruction instanceof MemAlloca) {
                    //记录当前偏移量，分配内存地址空间
                    String addrName = instruction.getName().substring(1);
                    if (((TypeAddr) instruction.getType()).getSrcType() instanceof TypeInt) {
                        parentFunction.addAddrs(addrName, 4);
                    } else if (((TypeAddr) instruction.getType()).getSrcType() instanceof TypeArray) {
                        int offset = ((TypeAddr) instruction.getType()).getSrcType().getSpace();
                        parentFunction.addAddrs(addrName, offset);
                    } else {
                        int offset = ((TypeAddr) instruction.getType()).getSrcType().getSpace();
                        parentFunction.addAddrs(addrName, offset);
                        parentFunction.addIndex(addrName);
                    }
                } else if (instruction instanceof MemGetelementptr) {
                    String base = instruction.getValue(0).getName().substring(1);
                    if (!parentFunction.getAddrs().containsKey(base)) {
                        La la = new La(new Reg(8), new Label(base));
                        mipsBlock.addInstruction(la);
                        this.buildGetelementptr(mipsBlock, parentFunction, instruction);
                        BinaryPure binaryPure;
                        int fp = parentFunction.getFp();
                        if (instruction.getNumofValues() != 3) {
                            binaryPure = new BinaryPure(BinaryPureType.subu, new Reg(15), new Reg(11), new Reg(30));
                            SaLSw sw = new SaLSw(new Reg(30), new Reg(15), new Imme(fp));
                            mipsBlock.addInstruction(binaryPure);
                            mipsBlock.addInstruction(sw);
                        } else {
                            binaryPure = new BinaryPure(BinaryPureType.subu, new Reg(15), new Reg(14), new Reg(30));
                            SaLSw sw = new SaLSw(new Reg(30), new Reg(15), new Imme(fp));
                            mipsBlock.addInstruction(binaryPure);
                            mipsBlock.addInstruction(sw);
                        }
                        String addr = instruction.getName().substring(1);
                        parentFunction.addAddrs(addr, 4);
                        parentFunction.addIndex(addr);
                    } else {
                        int offset = parentFunction.getAddrs().get(base);
                        if (parentFunction.getIndexArray().contains(base)) {
                            SaLLw lw = new SaLLw(new Reg(30), new Reg(8), new Imme(offset));
                            mipsBlock.addInstruction(lw);
                        } else {
                            Li li = new Li(new Reg(8), new Imme(offset));
                            mipsBlock.addInstruction(li);
                        }
                        this.buildGetelementptr(mipsBlock, parentFunction, instruction);
                        int fp = parentFunction.getFp();
                        if (instruction.getNumofValues() != 3) {
                            SaLSw sw = new SaLSw(new Reg(30), new Reg(11), new Imme(fp));
                            mipsBlock.addInstruction(sw);
                        } else {
                            SaLSw sw = new SaLSw(new Reg(30), new Reg(14), new Imme(fp));
                            mipsBlock.addInstruction(sw);
                        }
                        String addr = instruction.getName().substring(1);
                        parentFunction.addAddrs(addr, 4);
                        parentFunction.addIndex(addr);
                    }
                } else if (instruction instanceof MemLoad) {
                    String addr = instruction.getValue(0).getName().substring(1);
                    if (!parentFunction.getAddrs().containsKey(addr)) { //没有使用过的全局变量，通过标签直接寻址
                        int fp = parentFunction.getFp();
                        SaLLw lw = new SaLLw(new Reg(8), new Label(addr));                  // lw $t0, "addr"  将地址对应的值加载到$t0中
                        SaLSw sw = new SaLSw(new Reg(30), new Reg(8), new Imme(fp));    // sw $t0, Imme($30)  计数器fp和栈指针相加得到地址，把上述值存入内存
                        mipsBlock.addInstruction(lw);
                        mipsBlock.addInstruction(sw);
                        String name = instruction.getName().substring(1);
                        parentFunction.addVarName(name, 4);                             //存入function，栈增长
                    } else {    //使用过的变量
                        int offset = parentFunction.getAddrs().get(addr);
                        if (parentFunction.getIndexArray().contains(addr) && !(instruction.getType() instanceof TypeAddr)) {    //数组元素
                            SaLLw lw1 = new SaLLw(new Reg(30), new Reg(9), new Imme(offset));     //获得偏移量:lw $t1, 0($fp)
                            BinaryPure binaryPure = new BinaryPure(BinaryPureType.addu, new Reg(10), new Reg(9), new Reg(30));  //获得偏移量:addu $t2, $t1, $fp
                            SaLLw lw2 = new SaLLw(new Reg(10), new Reg(8), new Imme(0));    //从具体地址取出数值:lw $t0, 0($t2)
                            mipsBlock.addInstruction(lw1);
                            mipsBlock.addInstruction(binaryPure);
                            mipsBlock.addInstruction(lw2);
                        } else {    //普通变量，通过offset从内存中取出即可
                            SaLLw lw = new SaLLw(new Reg(30), new Reg(8), new Imme(offset));    //lw $t0, 0($fp)
                            mipsBlock.addInstruction(lw);
                        }
                        int fp = parentFunction.getFp();
                        SaLSw sw = new SaLSw(new Reg(30), new Reg(8), new Imme(fp));
                        mipsBlock.addInstruction(sw);
                        String name = instruction.getName().substring(1);
                        if (instruction.getType() instanceof TypeAddr) {
                            parentFunction.addAddrs(name, 4);
                            parentFunction.addIndex(name);
                        } else {
                            parentFunction.addVarName(name, 4);
                        }
                    }
                } else if (instruction instanceof MemStore) {
                    if (instruction.getValue(0) instanceof ConstInt) {         //存入int常数，可以直接取值
                        int val = ((ConstInt) instruction.getValue(0)).getVal();
                        Li li = new Li(new Reg(8), new Imme(val));               //用li指令直接赋值:li $t0, 0
                        mipsBlock.addInstruction(li);
                        String name = instruction.getValue(1).getName().substring(1);
                        if (!parentFunction.getAddrs().containsKey(name)) {         //未定义过，是全局变量
                            SaLSw sw = new SaLSw(new Reg(8), new Label(name));  //利用sw指令直接存到该地址:sw $t0, "name"
                            mipsBlock.addInstruction(sw);
                        } else {    //定义过或使用过，通过查找分配的内存空间name获得offset
                            int offset = parentFunction.getAddrs().get(name);
                            if (parentFunction.getIndexArray().contains(name)) {    //数组元素赋值
                                SaLLw lw = new SaLLw(new Reg(30), new Reg(9), new Imme(offset));  //在内存中加载地址:lw $t1, 4($fp)
                                BinaryPure binaryPure = new BinaryPure(BinaryPureType.addu, new Reg(10), new Reg(9), new Reg(30));  //得到实际地址:addu $t2, $t1, $fp
                                SaLSw sw = new SaLSw(new Reg(10), new Reg(8), new Imme(0)); //通过地址存储:sw $t0, 0($t2)
                                mipsBlock.addInstruction(lw);
                                mipsBlock.addInstruction(binaryPure);
                                mipsBlock.addInstruction(sw);
                            } else {        //普通变量赋值
                                SaLSw sw = new SaLSw(new Reg(30), new Reg(8), new Imme(offset));    //通过offset存入内存:sw $t0, 0($fp)
                                mipsBlock.addInstruction(sw);
                            }
                        }
                    } else {        //存入变量
                        String name = instruction.getValue(0).getName().substring(1);
                        int offset = parentFunction.getVarNames().get(name);
                        SaLLw lw = new SaLLw(new Reg(30), new Reg(8), new Imme(offset));    //根据offset将内存中变量的值加载到寄存器中：lw $t0, 4($fp)
                        mipsBlock.addInstruction(lw);
                        String addr = instruction.getValue(1).getName().substring(1);
                        if (!parentFunction.getAddrs().containsKey(addr)) { //全局变量，直接利用label寻址
                            SaLSw sw = new SaLSw(new Reg(8), new Label(addr));
                            mipsBlock.addInstruction(sw);
                        } else {    //局部变量，利用栈指针
                            int fp = parentFunction.getAddrs().get(addr);
                            if (parentFunction.getIndexArray().contains(addr) && !parentFunction.getIndexArray().contains(name)) {  //数组
                                lw = new SaLLw(new Reg(30), new Reg(9), new Imme(fp));
                                BinaryPure binaryPure = new BinaryPure(BinaryPureType.addu, new Reg(10), new Reg(9), new Reg(30));
                                SaLSw sw = new SaLSw(new Reg(10), new Reg(8), new Imme(0));
                                mipsBlock.addInstruction(lw);
                                mipsBlock.addInstruction(binaryPure);
                                mipsBlock.addInstruction(sw);
                            } else {    //普通变量
                                SaLSw sw = new SaLSw(new Reg(30), new Reg(8), new Imme(fp));    //sw $t0, 0($fp)
                                mipsBlock.addInstruction(sw);
                            }
                        }
                    }
                } else if (instruction instanceof MemZextTo) {
                    if (instruction.getValue(0) instanceof ConstInt) {  //常数直接赋值给寄存器
                        int val = ((ConstInt) instruction.getValue(0)).getVal();
                        Li li = new Li(new Reg(8), new Imme(val));
                        mipsBlock.addInstruction(li);
                    } else {    //否则根据栈指针查找，加载
                        String name = instruction.getValue(0).getName().substring(1);
                        int offset = parentFunction.getVarNames().get(name);
                        SaLLw lw = new SaLLw(new Reg(30), new Reg(8), new Imme(offset));
                        mipsBlock.addInstruction(lw);
                    }
                    //存入内存，栈增长
                    int fp = parentFunction.getFp();
                    SaLSw sw = new SaLSw(new Reg(30), new Reg(8), new Imme(fp));
                    mipsBlock.addInstruction(sw);
                    String name = instruction.getName().substring(1);
                    parentFunction.addVarName(name, 4);
                }
            } else if (instruction instanceof InstructonOfBinary) {
                if (instruction instanceof BinAdd) {
                    this.buildBinInstruction(BinaryPureType.addu, mipsBlock, parentFunction, instruction);
                } else if (instruction instanceof BinAnd) {
                    this.buildBinInstruction(BinaryPureType.and, mipsBlock, parentFunction, instruction);
                } else if (instruction instanceof BinIcmp) {
                    this.buildInstruction(mipsBlock, parentFunction, instruction);
                    BinIcmpType type = ((BinIcmp) instruction).getIcmpType();
                    if (type == BinIcmpType.EQ) {
                        Icmp icmp = new Icmp(IcmpType.seq, new Reg(10), new Reg(8), new Reg(9));
                        mipsBlock.addInstruction(icmp);
                    } else if (type == BinIcmpType.NE) {
                        Icmp icmp = new Icmp(IcmpType.sne, new Reg(10), new Reg(8), new Reg(9));
                        mipsBlock.addInstruction(icmp);
                    } else if (type == BinIcmpType.SGE) {
                        Icmp icmp = new Icmp(IcmpType.sge, new Reg(10), new Reg(8), new Reg(9));
                        mipsBlock.addInstruction(icmp);
                    } else if (type == BinIcmpType.SGT) {
                        Icmp icmp = new Icmp(IcmpType.sgt, new Reg(10), new Reg(8), new Reg(9));
                        mipsBlock.addInstruction(icmp);
                    } else if (type == BinIcmpType.SLE) {
                        Icmp icmp = new Icmp(IcmpType.sle, new Reg(10), new Reg(8), new Reg(9));
                        mipsBlock.addInstruction(icmp);
                    } else if (type == BinIcmpType.SLT) {
                        Icmp icmp = new Icmp(IcmpType.slt, new Reg(10), new Reg(8), new Reg(9));
                        mipsBlock.addInstruction(icmp);
                    }
                    int offset = parentFunction.getFp();
                    SaLSw sw = new SaLSw(new Reg(30), new Reg(10), new Imme(offset));
                    mipsBlock.addInstruction(sw);
                    String name = instruction.getName().substring(1);
                    parentFunction.addVarName(name, 4);
                } else if (instruction instanceof BinMul) {
                    this.buildInstruction(mipsBlock, parentFunction, instruction);
                    BinaryMulu mulu = new BinaryMulu(new Reg(10), new Reg(8), new Reg(9));
                    mipsBlock.addInstruction(mulu);
                    int offset = parentFunction.getFp();
                    SaLSw sw = new SaLSw(new Reg(30), new Reg(10), new Imme(offset));
                    mipsBlock.addInstruction(sw);
                    String name = instruction.getName().substring(1);
                    parentFunction.addVarName(name, 4);
                } else if (instruction instanceof BinOr) {
                    this.buildBinInstruction(BinaryPureType.or, mipsBlock, parentFunction, instruction);
                } else if (instruction instanceof  BinSdiv) {
                    this.buildInstruction(mipsBlock, parentFunction, instruction);
                    BinaryDiv div = new BinaryDiv(new Reg(8), new Reg(9));
                    Mflo mflo = new Mflo(new Reg(10));
                    mipsBlock.addInstruction(div);
                    mipsBlock.addInstruction(mflo);
                    int offset = parentFunction.getFp();
                    SaLSw sw = new SaLSw(new Reg(30), new Reg(10), new Imme(offset));
                    mipsBlock.addInstruction(sw);
                    String name = instruction.getName().substring(1);
                    parentFunction.addVarName(name, 4);
                } else if (instruction instanceof BinSrem) {
                    this.buildInstruction(mipsBlock, parentFunction, instruction);
                    BinaryDiv div = new BinaryDiv(new Reg(8), new Reg(9));
                    Mfhi mfhi = new Mfhi(new Reg(10));
                    mipsBlock.addInstruction(div);
                    mipsBlock.addInstruction(mfhi);
                    int offset = parentFunction.getFp();
                    SaLSw sw = new SaLSw(new Reg(30), new Reg(10), new Imme(offset));
                    mipsBlock.addInstruction(sw);
                    String name = instruction.getName().substring(1);
                    parentFunction.addVarName(name, 4);
                } else if (instruction instanceof BinSub) {
                    this.buildBinInstruction(BinaryPureType.subu, mipsBlock, parentFunction, instruction);
                }
            } else if (instruction instanceof InstructionOfTerminator) {
                if (instruction instanceof TermiBr) {
                    if (!((TermiBr) instruction).isCondition()) {   //无条件跳转
                        String name = instruction.getValue(0).getName();
                        J j = new J(new Label(name));               //直接使用j指令
                        mipsBlock.addInstruction(j);
                    } else {
                        if (instruction.getValue(0) instanceof ConstInt) {
                            int val = ((ConstInt) instruction.getValue(0)).getVal();
                            Li li = new Li(new Reg(8), new Imme(val));
                            mipsBlock.addInstruction(li);
                        } else {
                            String name = instruction.getValue(0).getName().substring(1);
                            int offset = parentFunction.getVarNames().get(name);
                            SaLLw lw = new SaLLw(new Reg(30), new Reg(8), new Imme(offset));
                            mipsBlock.addInstruction(lw);
                        }
                        //一条br指令拆解为两条跳转指令
                        //beq $t0, 1, Block_3
                        //j Block_2
                        String name1 = instruction.getValue(1).getName();
                        Beq beq = new Beq(new Reg(8), new Imme(1), new Label(name1));
                        mipsBlock.addInstruction(beq);
                        String name2 = instruction.getValue(2).getName();
                        J j = new J(new Label(name2));
                        mipsBlock.addInstruction(j);
                    }
                } else if (instruction instanceof TermiCall) {
                    if (!((Function) instruction.getValue(0)).isReserved()) {   //自定义函数
                        // 保存现场
                        // addi $sp, $sp, -4
                        // sw $ra, 0($sp)
                        BinaryImme binaryImme = new BinaryImme(BinaryImmeType.addi, new Reg(29), new Reg(29), new Imme(-4));
                        SaLSw sw = new SaLSw(new Reg(29), new Reg(31), new Imme(0));
                        mipsBlock.addInstruction(binaryImme);
                        mipsBlock.addInstruction(sw);
                        int cnt = ((Function) instruction.getValue(0)).getArguments().size();
                        int offset = parentFunction.getFp();
                        for (int i = 1; i <= cnt; i++) {
                            if (instruction.getValue(i) instanceof ConstInt) {
                                int val = ((ConstInt) instruction.getValue(i)).getVal();
                                Li li = new Li(new Reg(8), new Imme(val));
                                mipsBlock.addInstruction(li);
                            } else {
                                String name = instruction.getValue(i).getName().substring(1);
                                if (parentFunction.getVarNames().containsKey(name)) {
                                    //从内存中取出变量
                                    // lw $t0, 4($fp)
                                    int off1 = parentFunction.getVarNames().get(name);
                                    SaLLw lw = new SaLLw(new Reg(30), new Reg(8), new Imme(off1));
                                    mipsBlock.addInstruction(lw);
                                } else {
                                    int off1 = parentFunction.getAddrs().get(name);
                                    if (parentFunction.getIndexArray().contains(name)) {
                                        SaLLw lw = new SaLLw(new Reg(30), new Reg(9), new Imme(off1));
                                        BinaryImme binaryI = new BinaryImme(BinaryImmeType.addi, new Reg(8), new Reg(9), new Imme(-1 * offset));
                                        mipsBlock.addInstruction(lw);
                                        mipsBlock.addInstruction(binaryI);
                                    } else {
                                        Li li = new Li(new Reg(8), new Imme(off1));
                                        mipsBlock.addInstruction(li);
                                    }
                                }
                            }
                            sw = new SaLSw(new Reg(30), new Reg(8), new Imme(offset + 4 * (i - 1)));
                            mipsBlock.addInstruction(sw);
                        }
                        // 移动帧指针
                        BinaryImme binaryI = new BinaryImme(BinaryImmeType.addi, new Reg(30), new Reg(30), new Imme(offset));
                        mipsBlock.addInstruction(binaryI);
                        String name = instruction.getValue(0).getName().substring(1);
                        //跳转函数
                        Jal jal = new Jal(new Label(name));
                        mipsBlock.addInstruction(jal);
                        //恢复现场
                        // addi $fp, $fp, -12
                        // lw $ra, 0($sp)
                        // addi $sp, $sp, 4
                        binaryI = new BinaryImme(BinaryImmeType.addi, new Reg(30), new Reg(30), new Imme(-1 * offset));
                        mipsBlock.addInstruction(binaryI);
                        SaLLw lw = new SaLLw(new Reg(29), new Reg(31), new Imme(0));
                        binaryI = new BinaryImme(BinaryImmeType.addi, new Reg(29), new Reg(29), new Imme(4));
                        mipsBlock.addInstruction(lw);
                        mipsBlock.addInstruction(binaryI);
                        //存储返回值
                        // sw $v0, 12($fp)
                        if (((Function) instruction.getValue(0)).getReturnType() instanceof TypeInt) {
                            int fp = parentFunction.getFp();
                            sw = new SaLSw(new Reg(30), new Reg(2), new Imme(fp));
                            mipsBlock.addInstruction(sw);
                            name = instruction.getName().substring(1);
                            parentFunction.addVarName(name, 4);
                        }
                    } else {
                        if (Objects.equals(instruction.getValue(0).getName(), "@getint")) {
                            Li li = new Li(new Reg(2), new Imme(5));
                            Syscall syscall = new Syscall();
                            int fp = parentFunction.getFp();
                            SaLSw sw = new SaLSw(new Reg(30), new Reg(2), new Imme(fp));
                            mipsBlock.addInstruction(li);
                            mipsBlock.addInstruction(syscall);
                            mipsBlock.addInstruction(sw);
                            String name = instruction.getName().substring(1);
                            parentFunction.addVarName(name, 4);
                        } else if (Objects.equals(instruction.getValue(0).getName(), "@putch")) {
                            Li li = new Li(new Reg(2), new Imme(11));
                            mipsBlock.addInstruction(li);
                            li = new Li(new Reg(4), new Imme(((ConstInt) instruction.getValue(1)).getVal()));
                            mipsBlock.addInstruction(li);
                            Syscall syscall = new Syscall();
                            mipsBlock.addInstruction(syscall);
                        } else if (Objects.equals(instruction.getValue(0).getName(), "@putint")) {
                            Li li = new Li(new Reg(2), new Imme(1));
                            mipsBlock.addInstruction(li);
                            if (instruction.getValue(1) instanceof ConstInt) {
                                int val = ((ConstInt) instruction.getValue(1)).getVal();
                                li = new Li(new Reg(4), new Imme(val));
                                mipsBlock.addInstruction(li);
                            } else {
                                String name = instruction.getValue(1).getName().substring(1);
                                int fp = parentFunction.getVarNames().get(name);
                                SaLLw lw = new SaLLw(new Reg(30), new Reg(4), new Imme(fp));
                                mipsBlock.addInstruction(lw);
                            }
                            Syscall syscall = new Syscall();
                            mipsBlock.addInstruction(syscall);
                        }
                    }
                } else if (instruction instanceof TermiRet) {
                    if (((TermiRet) instruction).isVoid()) {
                        //无返回值，直接返回$ra即可
                        Jr jr = new Jr(new Reg(31));
                        mipsBlock.addInstruction(jr);
                    } else {
                        if (instruction.getValue(0) instanceof ConstInt) {
                            int val = ((ConstInt) instruction.getValue(0)).getVal();
                            // lw $v0, val
                            Li li = new Li(new Reg(2), new Imme(val));
                            mipsBlock.addInstruction(li);
                        } else {
                            String name = instruction.getValue(0).getName().substring(1);
                            int offset = parentFunction.getVarNames().get(name);
                            // lw $v0, 24($fp)
                            SaLLw lw = new SaLLw(new Reg(30), new Reg(2), new Imme(offset));
                            mipsBlock.addInstruction(lw);
                        }
                        if (Objects.equals(parentFunction.getFuncName(), "main")) {
                            Li li = new Li(new Reg(2), new Imme(10));
                            Syscall syscall = new Syscall();
                            mipsBlock.addInstruction(li);
                            mipsBlock.addInstruction(syscall);
                        } else {
                            Jr jr = new Jr(new Reg(31));
                            mipsBlock.addInstruction(jr);
                        }
                    }
                }
            }
        }
    }

    private void buildGetelementptr(MIPSBlock mipsBlock, MIPSFunction parentFunction, Instruction instruction) {
        if (instruction.getValue(1) instanceof ConstInt) {
            int val = ((ConstInt) instruction.getValue(1)).getVal();
            Li li = new Li(new Reg(9), new Imme(val));
            mipsBlock.addInstruction(li);
        } else {
            String name = instruction.getValue(1).getName().substring(1);
            int offset = parentFunction.getVarNames().get(name);
            SaLLw lw = new SaLLw(new Reg(30), new Reg(9), new Imme(offset));
            mipsBlock.addInstruction(lw);
        }
        BinaryMulu mulu = new BinaryMulu(new Reg(10), new Reg(9), new Imme(((TypeAddr) instruction.getValue(0).getType()).getSrcType().getSpace()));
        BinaryPure binaryPure = new BinaryPure(BinaryPureType.addu, new Reg(11), new Reg(8), new Reg(10));
        mipsBlock.addInstruction(mulu);
        mipsBlock.addInstruction(binaryPure);
        if (instruction.getNumofValues() == 3) {
            if (instruction.getValue(2) instanceof ConstInt) {
                int val = ((ConstInt) instruction.getValue(2)).getVal();
                Li li = new Li(new Reg(12), new Imme(val));
                mipsBlock.addInstruction(li);
            } else {
                String name = instruction.getValue(2).getName().substring(1);
                int offset = parentFunction.getVarNames().get(name);
                SaLLw lw = new SaLLw(new Reg(30), new Reg(12), new Imme(offset));
                mipsBlock.addInstruction(lw);
            }
            mulu = new BinaryMulu(new Reg(13), new Reg(12),
                    new Imme(((TypeArray) ((TypeAddr) instruction.getValue(0).getType()).getSrcType()).getTypeOfElement().getSpace()));
            binaryPure = new BinaryPure(BinaryPureType.addu, new Reg(14), new Reg(11), new Reg(13));
            mipsBlock.addInstruction(mulu);
            mipsBlock.addInstruction(binaryPure);
        }
    }

    private void buildBinInstruction(BinaryPureType binType, MIPSBlock mipsBlock, MIPSFunction parentFunction, Instruction instruction) {
        this.buildInstruction(mipsBlock, parentFunction, instruction);
        BinaryPure binaryPure = new BinaryPure(binType, new Reg(10), new Reg(8), new Reg(9));
        int offset = parentFunction.getFp();
        SaLSw sw = new SaLSw(new Reg(30), new Reg(10), new Imme(offset));
        mipsBlock.addInstruction(binaryPure);
        mipsBlock.addInstruction(sw);
        String name = instruction.getName().substring(1);
        parentFunction.addVarName(name, 4);
    }

    private void buildInstruction(MIPSBlock mipsBlock, MIPSFunction parentFunction, Instruction instruction) {
        if (instruction.getValue(0) instanceof ConstInt) {
            int val = ((ConstInt) instruction.getValue(0)).getVal();
            Li li = new Li(new Reg(8), new Imme(val));
            mipsBlock.addInstruction(li);
        } else {
            String name = instruction.getValue(0).getName().substring(1);
            int offset = parentFunction.getVarNames().get(name);
            SaLLw lw = new SaLLw(new Reg(30), new Reg(8), new Imme(offset));
            mipsBlock.addInstruction(lw);
        }
        if (instruction.getValue(1) instanceof ConstInt) {
            int val= ((ConstInt) instruction.getValue(1)).getVal();
            Li li = new Li(new Reg(9), new Imme(val));
            mipsBlock.addInstruction(li);
        } else {
            String name = instruction.getValue(1).getName().substring(1);
            int offset = parentFunction.getVarNames().get(name);
            SaLLw lw = new SaLLw(new Reg(30), new Reg(9), new Imme(offset));
            mipsBlock.addInstruction(lw);
        }
    }

    public Module getModule() {
        return this.module;
    }

    public MIPSModule getMipsModule() {
        return this.mipsModule;
    }

    public String getMipsString() {
        return this.mipsModule.toString();
    }
}
