package IRTools_llvm;

import IRTools_llvm.Type.Type;
import IRTools_llvm.Type.TypeAddr;
import IRTools_llvm.Type.TypeArray;
import IRTools_llvm.Type.TypeFunction;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Function.Function;
import IRTools_llvm.Value.Instructions.Binary.*;
import IRTools_llvm.Value.Instructions.Memory.*;
import IRTools_llvm.Value.Instructions.Terminator.TermiBr;
import IRTools_llvm.Value.Instructions.Terminator.TermiCall;
import IRTools_llvm.Value.Instructions.Terminator.TermiRet;
import IRTools_llvm.Value.Module;
import IRTools_llvm.Value.Value;

import java.util.ArrayList;

public class IRBuildFactory {
    public static int numCounter = 0;
    public static int blockCnt = 0;

    public static Function buildFunction(TypeFunction typeFunction, String name, boolean isReserved) {
        Function function = new Function(name, typeFunction, isReserved);
        numCounter = function.getArguments().size();
        blockCnt = 0;
        Module.getInstance().addFunction(function);
        return function;
    }

    public static BasicBlock buildBasicBlock(Function parent) {
        int count = blockCnt++;
        BasicBlock block = new BasicBlock("Function_" + parent.getName().substring(1) + "_BasicBlock_" + count, parent);
        parent.addBasicBlock(block);
        return block;
    }


    /*************************************Binary Instruction*************************************/

    public static BinAdd bulidBinAdd(Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinAdd binAdd = new BinAdd(String.valueOf(count), v1, v2, parent);
        parent.addInstruction(binAdd);
        return binAdd;
    }

    public static BinSub buildBinSub(Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinSub binSub = new BinSub(String.valueOf(count), v1, v2, parent);
        parent.addInstruction(binSub);
        return binSub;
    }

    public static BinMul buildBinMul(Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinMul binMul = new BinMul(String.valueOf(count), v1, v2, parent);
        parent.addInstruction(binMul);
        return binMul;
    }

    public static BinSdiv buildBinSdiv(Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinSdiv binSdiv = new BinSdiv(String.valueOf(count), v1, v2, parent);
        parent.addInstruction(binSdiv);
        return binSdiv;
    }

    public static BinSrem buildBinSrem(Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinSrem binSrem = new BinSrem(String.valueOf(count), v1, v2, parent);
        parent.addInstruction(binSrem);
        return binSrem;
    }

    public static BinAnd buildBinAnd(Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinAnd binAnd = new BinAnd(String.valueOf(count), v1, v2, parent);
        parent.addInstruction(binAnd);
        return binAnd;
    }

    public static BinOr buildBinOr(Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinOr binOr = new BinOr(String.valueOf(count), v1, v2, parent);
        parent.addInstruction(binOr);
        return binOr;
    }

    public static BinIcmp buildBinIcmp(BinIcmpType icmpType, Value v1, Value v2, BasicBlock parent) {
        int count = numCounter++;
        BinIcmp binIcmp = new BinIcmp(String.valueOf(count), icmpType, v1, v2, parent);
        parent.addInstruction(binIcmp);
        return binIcmp;
    }


    /*************************************Memory Instruction*************************************/

    public static MemAlloca bulidMemAlloca(Type allocatedType, BasicBlock parent) {
        int count = numCounter++;
        MemAlloca memAlloca = new MemAlloca(String.valueOf(count), allocatedType, parent);
        parent.addInstruction(memAlloca);
        return memAlloca;
    }

    public static MemLoad buildMemLoad(Type type, Value addr, BasicBlock parent) {
        int count = numCounter++;
        MemLoad memLoad = new MemLoad(String.valueOf(count), type, addr, parent);
        parent.addInstruction(memLoad);
        return memLoad;
    }

    public static void buildMemStore(Value storeVal, Value addr, BasicBlock parent) {
        MemStore memStore = new MemStore(storeVal, addr, parent);
        parent.addInstruction(memStore);
    }

    public static MemGetelementptr buildMemGetelementptr(TypeAddr type, Value base, Value pointIndex, BasicBlock parent) {
        int count = numCounter++;
        MemGetelementptr memGetelementptr = new MemGetelementptr(String.valueOf(count), type, base, pointIndex, parent);
        parent.addInstruction(memGetelementptr);
        return memGetelementptr;
    }

    public static MemGetelementptr buildMemGetelementptr(TypeArray type, Value base, Value pointIndex1, Value pointIndex2, BasicBlock parent) {
        int count = numCounter++;
        MemGetelementptr memGetelementptr = new MemGetelementptr(String.valueOf(count), type, base, pointIndex1, pointIndex2, parent);
        parent.addInstruction(memGetelementptr);
        return memGetelementptr;
    }

    public static MemZextTo buildZextTo(Type toType, Value zextVal, BasicBlock parent) {
        int count = numCounter++;
        MemZextTo memZextTo = new MemZextTo(String.valueOf(count), toType, zextVal, parent);
        parent.addInstruction(memZextTo);
        return memZextTo;
    }

    /*************************************Terminator Instruction*************************************/

    public static void buildTermiBr(BasicBlock block, BasicBlock parent) {
        TermiBr termiBr = new TermiBr(block, parent);
        parent.addInstruction(termiBr);
    }

    public static void buildTermiBr(Value condition, BasicBlock b1, BasicBlock b2, BasicBlock parent) {
        TermiBr termiBr = new TermiBr(condition, b1, b2, parent);
        parent.addInstruction(termiBr);
    }

    public static TermiCall buildTermiCallVoid(Function function, ArrayList<Value> args, BasicBlock parent) {
        TermiCall termiCall = new TermiCall(function, args, parent);
        parent.addInstruction(termiCall);
        return termiCall;
    }

    public static TermiCall buildTermiCall(Function function, ArrayList<Value> args, BasicBlock parent) {
        int count = numCounter++;
        TermiCall termiCall = new TermiCall(String.valueOf(count), function, args, parent);
        parent.addInstruction(termiCall);
        return termiCall;
    }

    public static void buildTermiRet(BasicBlock parent) {
        TermiRet termiRet = new TermiRet(parent);
        parent.addInstruction(termiRet);
    }

    public static TermiRet buildTermiRet(Value retValue, BasicBlock parent) {
        TermiRet termiRet = new TermiRet(retValue, parent);
        parent.addInstruction(termiRet);
        return termiRet;
    }

}
