package IRTools_llvm;

import GrammaticalTools.GrammaticalTreeDef.*;
import IRTools_llvm.IRBuildFactory;
import IRTools_llvm.Type.*;
import IRTools_llvm.Value.BasicBlock.BasicBlock;
import IRTools_llvm.Value.Constant.Const;
import IRTools_llvm.Value.Constant.ConstArray;
import IRTools_llvm.Value.Constant.ConstInt;
import IRTools_llvm.Value.Constant.GlobalVar;
import IRTools_llvm.Value.Function.Function;
import IRTools_llvm.Value.Instructions.Binary.BinIcmpType;
import IRTools_llvm.Value.Instructions.Memory.MemAlloca;
import IRTools_llvm.Value.Instructions.Memory.MemGetelementptr;
import IRTools_llvm.Value.Instructions.Terminator.TermiBr;
import IRTools_llvm.Value.Instructions.Terminator.TermiRet;
import IRTools_llvm.Value.Module;
import IRTools_llvm.Value.Value;
import LexicalTools.wordTuple;

import java.util.ArrayList;
import java.util.HashMap;

public class Visitor {
    private final NodeCompUnit compUnit;
    private boolean isConst = false;
    private boolean isInit = false;
    private boolean isCall = false;
    private boolean isCruit = false;
    private boolean isInitArray = false;
    private Value curVal;
    private Function curFunction;
    private BasicBlock curBasicBlock;
    private BasicBlock curIfBlock;
    private BasicBlock curElseBlock;
    private BasicBlock curNextOrBlock;
    private BasicBlock curNextAndBlock;
    private BasicBlock curBreakBlock;
    private BasicBlock curContinueBlock;
    private int curInt;
    private Type curType;
    private ArrayList<Integer> curDims;
    private ArrayList<Value> curArray;
    private ArrayList<Type> curTypeArray;
    private int curArrayIndex;
    private int symbolIndex = 0;
    private boolean creatingTables;
    private final ArrayList<HashMap<String, Value>> symbolTables = new ArrayList<>();

    public Visitor(NodeCompUnit compUnit) {
        symbolTables.add(new HashMap<>());
        this.compUnit = compUnit;
        //this.visitNodeCompUnit();
    }

    public void storeVal(String name, Value value) {
        HashMap<String, Value> map = this.symbolTables.get(this.symbolIndex);
        map.put(name, value);
    }

    public Value getSymbolVal(String name) {
        for (int i = this.symbolIndex;i >= 0;i--) {
            if (this.symbolTables.get(i).containsKey(name)) {
                return this.symbolTables.get(i).get(name);
            }
        }
        return null;
    }

    public void visitNodeCompUnit() {                                   //编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
        ArrayList<NodeDecl>  decls = this.compUnit.getDecls();
        ArrayList<NodeFuncDef> funcDefs = this.compUnit.getFuncDefs();
        NodeMainFuncDef mainFuncDef = this.compUnit.getMainFuncDef();
        for (NodeDecl decl: decls) {
            this.visitNodeDecl(decl);
        }
        for (NodeFuncDef funcDef: funcDefs) {
            this.visitNodeFuncDef(funcDef);
        }
        this.visitNodeMainFuncDef(mainFuncDef);
    }

    private void visitNodeDecl(NodeDecl decl) {                         //声明 Decl → ConstDecl | VarDecl
        NodeConstDecl constDecl = decl.getConstDecl();
        NodeVarDecl varDecl = decl.getVarDecl();
        if (varDecl == null) {
            this.visitConstDecl(constDecl);
        } else {
            this.visitNodeVarDecl(varDecl);
        }
    }

    private void visitConstDecl(NodeConstDecl constDecl) {          //常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        ArrayList<NodeConstDef> constDefs = constDecl.getConstDefs();
        for (NodeConstDef constDef: constDefs) {
            this.visitNodeConstDef(constDef);
        }
    }

    private void visitNodeConstDef(NodeConstDef constDef) {             //常数定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        wordTuple ident = constDef.getIdent();
        ArrayList<NodeConstExp> constExps = constDef.getConstExps();
        NodeConstInitVal constInitVal = constDef.getConstInitVal();
        String name = ident.getWordName();
        if (constExps.isEmpty()) {          //0维数组（常数）
            if (this.symbolIndex == 0) {    //全局
                this.isInit = true;
                this.visitNodeConstInitVal(constInitVal);
                this.isInit = false;
                GlobalVar globalVar = new GlobalVar(name, true, (Const) this.curVal);
                this.storeVal(name, globalVar);
                Module.getInstance().addGlobalVar(globalVar);
            } else {
                MemAlloca memAlloca = IRBuildFactory.bulidMemAlloca(new TypeInt(32), this.curBasicBlock);
                this.storeVal(name, memAlloca);
                this.isInitArray = true;
                this.visitNodeConstInitVal(constInitVal);
                this.isInitArray = false;
                IRBuildFactory.buildMemStore(this.curVal, memAlloca, this.curBasicBlock);
            }
        } else {                            //数组
            ArrayList<Integer> dims = new ArrayList<>();
            for (NodeConstExp constExp: constExps) { //如果有a[2][3]，那么得到的dims:{2, 3}
                this.isConst = true;
                this.visitNodeConstExp(constExp);
                this.isConst = false;
                dims.add(((ConstInt) curVal).getVal());
            }
            Type type = new TypeInt(32);
            for (int i = dims.size() - 1;i >= 0;i--) {
                type = new TypeArray(dims.get(i), type);
            }
            if (this.symbolIndex == 0) {    //符号表索引为0， 全局常量
                this.curDims = new ArrayList<>(dims);
                this.isInit = true;
                this.isInitArray = true;
                this.visitNodeConstInitVal(constInitVal);
                this.isInitArray = false;
                this.isInit = false;
                ArrayList<Const> consts = new ArrayList<>();
                for (Value value: this.curArray) {
                    consts.add((ConstInt) value);
                }
                int data = 1;
                for (Integer dim: dims) {
                    data = dim * data;
                }
                for (int i = dims.size() - 1;i > 0;i--) {
                    int curDim = dims.get(i);
                    data = data / dims.get(i);
                    ArrayList<Const> list = new ArrayList<>();
                    for (int j = 0;j < data;j++) {
                        ArrayList<Const> constArrayList = new ArrayList<>();
                        for (int k = 0;k < curDim;k++) {
                            constArrayList.add(consts.get(j * curDim + k));
                        }
                        list.add(new ConstArray(constArrayList));
                    }
                    consts = list;
                }
                Const constArray = new ConstArray(consts);
                GlobalVar globalVar = new GlobalVar(name, true, constArray);
                this.storeVal(name, globalVar);
                Module.getInstance().addGlobalVar(globalVar);
            } else {                        //局部变量local
                MemAlloca memAlloca = IRBuildFactory.bulidMemAlloca(type, this.curBasicBlock);
                this.storeVal(name, memAlloca);
                this.curDims = new ArrayList<>(dims);
                this.isInitArray = true;
                this.visitNodeConstInitVal(constInitVal);
                this.isInitArray = false;
                ArrayList<Value> values = new ArrayList<>(this.curArray);
                this.curArrayIndex = 0;
                this.initArray(type, dims, values, memAlloca, 1);
                this.curArrayIndex = 0;
            }
        }
    }

    private void visitNodeConstInitVal(NodeConstInitVal constInitVal) {  //常量初值 ConstInitVal → ConstExp  | '{' [ ConstInitVal { ',' ConstInitVal } ] '}' /
        NodeConstExp constExp = constInitVal.getConstExp();
        ArrayList<NodeConstInitVal> constInitVals = constInitVal.getConstInitVals();
        if (constExp != null) {                 //普通变量初值
            if (this.isInit || !this.isInitArray) {
                this.isConst = true;
                this.visitNodeConstExp(constExp);
                this.isConst = false;
            } else {
                this.visitNodeConstExp(constExp);
            }
        } else {
            ArrayList<Value> values = new ArrayList<>();
            for (NodeConstInitVal nodeConstInitVal: constInitVals) {
                if (nodeConstInitVal.getConstExp() != null) {
                    this.visitNodeConstInitVal(nodeConstInitVal);
                    values.add(this.curVal);
                } else {
                    this.curDims = new ArrayList<Integer>() {{
                        add(curDims.remove(0));
                    }};
                    this.visitNodeConstInitVal(nodeConstInitVal);
                    values.addAll(this.curArray);
                }
            }
            this.curArray = values;
        }
    }

    private void visitNodeVarDecl(NodeVarDecl varDecl) {    //变量声明 VarDecl → BType VarDef { ',' VarDef } ';' // 1.花括号内重复0次 2.花括号内重复多次
        ArrayList<NodeVarDef> varDefs = varDecl.getVarDefs();
        for (NodeVarDef varDef: varDefs) {
            this.visitNodeVarDef(varDef);
        }
    }

    private void visitNodeVarDef(NodeVarDef varDef) {   //变量定义 VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义| Ident { '[' ConstExp ']' } '=' InitVal
        wordTuple ident = varDef.getIdent();
        ArrayList<NodeConstExp> constExps = varDef.getConstExps();
        NodeInitVal initVal = varDef.getInitVal();
        String name = ident.getWordName();
        if (constExps.isEmpty()) {  //普通变量
            if (this.symbolIndex == 0) {    //全局
                GlobalVar globalVar;
                if (initVal == null) {          //无初值
                    globalVar = new GlobalVar(name, new TypeInt(32));
                } else {                        //有初值
                    this.isInit = true;
                    this.visitNodeInitVal(initVal);
                    this.isInit = false;
                    globalVar = new GlobalVar(name, false, (Const) this.curVal);
                }
                this.storeVal(name, globalVar);
                Module.getInstance().addGlobalVar(globalVar);
            } else {                        //局部
                MemAlloca memAlloca = IRBuildFactory.bulidMemAlloca(new TypeInt(32), this.curBasicBlock);
                this.storeVal(name, memAlloca);
                if (initVal != null) {
                    this.visitNodeInitVal(initVal);
                    IRBuildFactory.buildMemStore(this.curVal, memAlloca, this.curBasicBlock);
                }
            }
        } else {                    //数组
            ArrayList<Integer> dims = new ArrayList<>();
            for (NodeConstExp constExp: constExps) { //如果有a[2][3]，那么得到的dims:{2, 3}
                this.isConst = true;
                this.visitNodeConstExp(constExp);
                this.isConst = false;
                dims.add(((ConstInt) curVal).getVal());
            }
            Type type = new TypeInt(32);
            for (int i = dims.size() - 1;i >= 0;i--) {
                type = new TypeArray(dims.get(i), type);
            }
            if (this.symbolIndex == 0) {    //全局
                GlobalVar globalVar;
                if (initVal == null) {             //无初值
                    globalVar = new GlobalVar(name, type);
                } else {                           //有初值
                    this.curDims = dims;
                    this.isInit = true;
                    this.visitNodeInitVal(initVal);
                    this.isInit = false;
                    ArrayList<Const> consts = new ArrayList<>();
                    for (Value value: this.curArray) {
                        consts.add((ConstInt) value);
                    }
                    int data = 1;
                    for (Integer dim: dims) {
                        data = dim * data;
                    }
                    for (int i = dims.size() - 1;i > 0;i--) {
                        int curDim = dims.get(i);
                        data = data / dims.get(i);
                        ArrayList<Const> list = new ArrayList<>();
                        for (int j = 0;j < data;j++) {
                            ArrayList<Const> constArrayList = new ArrayList<>();
                            for (int k = 0;k < curDim;k++) {
                                constArrayList.add(consts.get(j * curDim + k));
                            }
                            list.add(new ConstArray(constArrayList));
                        }
                        consts = list;
                    }
                    Const initVal1 = new ConstArray(consts);
                    globalVar = new GlobalVar(name, false, initVal1);
                }
                this.storeVal(name, globalVar);
                Module.getInstance().addGlobalVar(globalVar);
            } else {                        //局部
                MemAlloca memAlloca = IRBuildFactory.bulidMemAlloca(type, this.curBasicBlock);
                this.storeVal(name, memAlloca);
                if (initVal != null) {
                    this.curDims = dims;
                    this.visitNodeInitVal(initVal);
                    ArrayList<Value> values = new ArrayList<>(this.curArray);
                    this.curArrayIndex = 0;
                    initArray(type, dims, values, memAlloca, 1);
                    this.curArrayIndex = 0;
                }
            }
        }
    }

    private void initArray(Type type,ArrayList<Integer> dims, ArrayList<Value> values, Value addr, int depth) {
        for (int i = 0; i < dims.get(depth - 1); i++) {
            MemGetelementptr memGetelementptr = IRBuildFactory.
                    buildMemGetelementptr(((TypeArray) type),  addr, new ConstInt(32, 0), new ConstInt(32, i), this.curBasicBlock);
            if (depth == dims.size()) {
                IRBuildFactory.buildMemStore(values.get(this.curArrayIndex++), memGetelementptr, this.curBasicBlock);
            } else {
                this.initArray(((TypeArray) type).getTypeOfElement(), dims, values, memGetelementptr, depth + 1);
            }
        }
    }

    private void visitNodeInitVal(NodeInitVal initVal) {    //变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'// 1.表达式初值 2.一维数组初值 3.二维数组初值
        NodeExp exp = initVal.getExp();
        ArrayList<NodeInitVal> initVals = initVal.getInitVals();
        if (exp != null) {              //Exp
            if (!this.isInit) {
                this.visitNodeExp(exp);
            } else {
                this.isConst = true;
                this.visitNodeExp(exp);
                this.isConst = false;
                this.curVal = new ConstInt(32, this.curInt);
            }
        } else {                        //'{' [ InitVal { ',' InitVal } ] '}'
            if (initVals != null) {
                ArrayList<Value> values = new ArrayList<>();
                for (NodeInitVal nodeInitVal: initVals) {
                    NodeExp nodeExp = nodeInitVal.getExp();
                    if (nodeExp != null) {
                        if (!this.isInit) {
                            this.visitNodeInitVal(nodeInitVal);
                        } else {
                            this.isConst = true;
                            this.visitNodeInitVal(nodeInitVal);
                            this.isConst = false;
                        }
                        values.add(this.curVal);
                    }  else {
                        int dim = this.curDims.get(0);
                        this.curDims = new ArrayList<>();
                        curDims.add(dim);
                        this.visitNodeInitVal(nodeInitVal);
                        values.addAll(this.curArray);
                    }
                }
                this.curArray = values;
            }
        }
    }

    private void visitNodeFuncDef(NodeFuncDef funcDef) {    //函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // 1.无形参 2.有形参
        NodeFuncType funcType = funcDef.getFuncType();
        wordTuple ident = funcDef.getIdent();
        NodeFuncFParams funcFParams = funcDef.getFuncFParams();
        NodeBlock block = funcDef.getBlock();
        String name = ident.getWordName();
        ArrayList<Type> paramsTypes = new ArrayList<>();
        if (funcFParams != null) {
            this.visitNodeFuncFParams(funcFParams);
            paramsTypes.addAll(this.curTypeArray);
        }
        TypeFunction typeFunction;
        if (funcType.getType().isVoid()) {      //void
            typeFunction = new TypeFunction(paramsTypes, new TypeVoid());
        } else {                                //int
            typeFunction = new TypeFunction(paramsTypes, new TypeInt(32));
        }
        this.curFunction = IRBuildFactory.buildFunction(typeFunction, name, false);
        this.curBasicBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
        this.creatingTables = true;
        this.symbolIndex++;
        this.symbolTables.add(new HashMap<>());
        if (funcFParams != null) {
            ArrayList<NodeFuncFParam> nodeFuncFParams = funcFParams.getFuncFParams();
            int cnt = 0;
            for (NodeFuncFParam funcFParam: nodeFuncFParams) {
                MemAlloca memAlloca;
                if (funcFParam.getConstExps() != null) {            //数组参数
                    Type type = new TypeInt(32);
                    for (int i = funcFParam.getConstExps().size() - 1; i >= 0; i--) {
                        this.isConst = true;
                        this.visitNodeConstExp(funcFParam.getConstExps().get(i));
                        this.isConst = false;
                        type = new TypeArray(this.curInt, type);
                    }
                    memAlloca = IRBuildFactory.bulidMemAlloca(new TypeAddr(type), this.curBasicBlock);
                } else {                                           //普通变量参数
                    memAlloca = IRBuildFactory.bulidMemAlloca(new TypeInt(32), this.curBasicBlock);
                }
                IRBuildFactory.buildMemStore(this.curFunction.getArguments().get(cnt), memAlloca, this.curBasicBlock);
                this.storeVal(funcFParam.getIdent().getWordName(), memAlloca);
                cnt++;
            }
        }
        this.visitNodeBlock(block);
        if (this.curFunction.getReturnType() instanceof TypeVoid) {
            if (this.curBasicBlock.getInstructions().isEmpty()) {
                IRBuildFactory.buildTermiRet(this.curBasicBlock);
            } else {
                if (!(this.curBasicBlock.getInstructions().get(this.curBasicBlock.getInstructions().size() - 1) instanceof TermiRet)) {
                    IRBuildFactory.buildTermiRet(this.curBasicBlock);
                }
            }
        }
    }

    private void visitNodeMainFuncDef(NodeMainFuncDef mainFuncDef) {    //主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block // 存在main函数
        String name = "main";
        NodeBlock block = mainFuncDef.getBlock();
        TypeFunction typeFunction = new TypeFunction(new ArrayList<>(), new TypeInt(32));
        this.curFunction = IRBuildFactory.buildFunction(typeFunction, name, false);
        this.curBasicBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
        this.creatingTables = true;
        this.symbolIndex++;
        this.symbolTables.add(new HashMap<>());
        this.visitNodeBlock(block);
    }

    private void visitNodeFuncFParams(NodeFuncFParams funcFParams) {    //函数形参表 FuncFParams → FuncFParam { ',' FuncFParam } // 1.花括号内重复0次 2.花括号内重复多次
        ArrayList<Type> paramsTypes = new ArrayList<>();
        ArrayList<NodeFuncFParam> nodeFuncFParams = funcFParams.getFuncFParams();
        for (NodeFuncFParam funcFParam: nodeFuncFParams) {
            this.visitNodeFuncFParam(funcFParam);
            paramsTypes.add(this.curType);
        }
        this.curTypeArray = paramsTypes;
    }

    private void visitNodeFuncFParam(NodeFuncFParam funcFParam) {       //函数形参 FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }] // 1.普通变量2.一维数组变量 3.二维数组变量
        ArrayList<NodeConstExp> constExps = funcFParam.getConstExps();
        if (constExps == null) {        //普通变量参数
            this.curType = new TypeInt(32);
        } else {                        //数组参数
            Type type = new TypeInt(32);
            for (int i = constExps.size() - 1; i >= 0; i--) {
                this.isConst = true;
                this.visitNodeConstExp(constExps.get(i));
                this.isConst = false;
                type = new TypeArray(this.curInt, type);
            }
            this.curType = new TypeAddr(type);
        }
    }

    private void visitNodeBlock(NodeBlock block) {  //语句块 Block → '{' { BlockItem } '}' // 1.花括号内重复0次 2.花括号内重复多次
        ArrayList<NodeBlockItem> blockItems = block.getBlockItems();
        if (!this.creatingTables) {
            this.symbolTables.add(new HashMap<>());
            this.symbolIndex++;
        } else {
            this.creatingTables = false;
        }
        for (NodeBlockItem blockItem: blockItems) {
            this.visitNodeBlockItem(blockItem);
        }
        this.symbolTables.remove(this.symbolIndex--);
    }

    private void visitNodeBlockItem(NodeBlockItem blockItem) {  //语句块项 BlockItem → Decl | Stmt // 覆盖两种语句块项
        NodeDecl decl = blockItem.getDecl();
        NodeStmt stmt = blockItem.getStmt();
        if (decl != null) {
            this.visitNodeDecl(decl);
        } else {
            this.visitNodeStmt(stmt);
        }
    }

    private void visitNodeStmt(NodeStmt stmt) {
        if (stmt instanceof NodeStmtLv) {               //LVal '=' Exp ';'
            this.visitNodeStmtLv((NodeStmtLv) stmt);
        } else if (stmt instanceof NodeStmtEx) {        // [Exp] ';'
            this.visitNodeStmtEx((NodeStmtEx) stmt);
        } else if (stmt instanceof NodeStmtBl) {        //Block
            this.visitNodeStmtBl((NodeStmtBl) stmt);
        } else if (stmt instanceof NodeStmtIf) {        //'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
            this.visitNodeStmtIf((NodeStmtIf) stmt);
        } else if (stmt instanceof NodeStmtFo) {        //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
            this.visitNodeStmtFo((NodeStmtFo) stmt);
        } else if (stmt instanceof NodeStmtBr) {        //'break' ';' | 'continue' ';'
            this.visitStmtBr((NodeStmtBr) stmt);
        } else if (stmt instanceof NodeStmtRe) {        //'return' [Exp] ';' // 1.有Exp 2.无Exp
            this.visitNodeStmtRe((NodeStmtRe) stmt);
        } else if (stmt instanceof NodeStmtGe) {        //LVal '=' 'getint''('')'';'
            this.visitNodeStmtGe((NodeStmtGe) stmt);
        } else if (stmt instanceof NodeStmtPr) {        //'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
            this.visitNodeStmtPr((NodeStmtPr) stmt);
        }
    }

    private void visitNodeStmtLv(NodeStmtLv stmtLv) {   //LVal '=' Exp ';'
        NodeLVal lv = stmtLv.getlVal();
        NodeExp exp = stmtLv.getExp();
        this.visitNodeLVal(lv);
        Value left = this.curVal;
        this.visitNodeExp(exp);
        Value right = this.curVal;
        IRBuildFactory.buildMemStore(right, left, this.curBasicBlock);
    }

    private void visitNodeStmtEx(NodeStmtEx stmtEx) {   // [Exp] ';'
        NodeExp exp = stmtEx.getExp();
        if (exp != null) {
            this.visitNodeExp(exp);
        }
    }

    private void visitNodeStmtBl(NodeStmtBl stmtBl) {   //Block
        NodeBlock block = stmtBl.getBlock();
        this.visitNodeBlock(block);
    }

    private void visitNodeStmtIf(NodeStmtIf stmtIf) {   //'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
        NodeCond cond = stmtIf.getCond();
        NodeStmt stmt1 = stmtIf.getStmtIf();
        NodeStmt stmt2 = stmtIf.getStmtElse();
        BasicBlock blockIf = IRBuildFactory.buildBasicBlock(this.curFunction);
        BasicBlock blockElse = IRBuildFactory.buildBasicBlock(this.curFunction);
        BasicBlock nextBlock;
        if (stmt2 != null) {
            nextBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
        } else {
            nextBlock = blockElse;
        }
        this.curIfBlock = blockIf;
        this.curElseBlock = blockElse;
        this.visitNodeCond(cond);
        this.curBasicBlock = this.curIfBlock;
        this.visitNodeStmt(stmt1);
        boolean ifReturn = false;
        boolean elseReturn = false;
        if (!this.curBasicBlock.getInstructions().isEmpty()) {
            if (this.curBasicBlock.getInstructions().get(this.curBasicBlock.getInstructions().size() - 1) instanceof TermiRet) {
                ifReturn = true;
            } else if (this.curBasicBlock.getInstructions().get(this.curBasicBlock.getInstructions().size() - 1) instanceof TermiBr) {
                ifReturn = true;
            } else {
                IRBuildFactory.buildTermiBr(nextBlock, this.curBasicBlock);
            }
        } else {
            IRBuildFactory.buildTermiBr(nextBlock, this.curBasicBlock);
        }
        if (stmt2 != null) {
            this.curBasicBlock = blockElse;
            this.visitNodeStmt(stmt2);
            if (!this.curBasicBlock.getInstructions().isEmpty()) {
                if (this.curBasicBlock.getInstructions().get(this.curBasicBlock.getInstructions().size() - 1) instanceof TermiRet) {
                    elseReturn = true;
                } else if (this.curBasicBlock.getInstructions().get(this.curBasicBlock.getInstructions().size() - 1) instanceof TermiBr) {
                    elseReturn = true;
                } else {
                    IRBuildFactory.buildTermiBr(nextBlock, this.curBasicBlock);
                }
            } else {
                IRBuildFactory.buildTermiBr(nextBlock, this.curBasicBlock);
            }
        }
        this.curBasicBlock = nextBlock;
        //if (!ifReturn || !elseReturn) {

        //}
        /*
        if (ifReturn && elseReturn) {
            this.curFunction.getBasicBlocks().remove(nextBlock);
        } else {
            this.curBasicBlock = nextBlock;
        }
        */
    }

    private void visitNodeStmtFo(NodeStmtFo stmtFo) {   //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
        NodeForStmt forStmt1 = stmtFo.getForStmt1();
        NodeCond cond = stmtFo.getCond();
        NodeForStmt forStmt2 = stmtFo.getForStmt2();
        NodeStmt stmt = stmtFo.getStmt();
        if (forStmt1 != null) {
            this.visitNodeForStmt(forStmt1);
        }
        BasicBlock condBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
        BasicBlock stmtBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
        BasicBlock forStmt2Block = IRBuildFactory.buildBasicBlock(this.curFunction);
        BasicBlock nextBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
        IRBuildFactory.buildTermiBr(condBlock, this.curBasicBlock);
        BasicBlock breakBasicBlock = this.curBreakBlock;
        BasicBlock continueBasicBlock = this.curContinueBlock;
        this.curIfBlock = stmtBlock;
        this.curElseBlock = nextBlock;
        this.curBasicBlock = condBlock;
        if (cond != null) {
            this.visitNodeCond(cond);
        } else {
            IRBuildFactory.buildTermiBr(stmtBlock, this.curBasicBlock);
        }
        this.curBreakBlock = nextBlock;
        this.curContinueBlock = forStmt2Block;
        this.curBasicBlock = stmtBlock;
        this.visitNodeStmt(stmt);
        if (this.curBasicBlock.getInstructions().isEmpty()) {
            IRBuildFactory.buildTermiBr(forStmt2Block, this.curBasicBlock);
        } else {
            if (!(this.curBasicBlock.getInstructions().get(this.curBasicBlock.getInstructions().size() - 1) instanceof TermiBr)) {
                IRBuildFactory.buildTermiBr(forStmt2Block, this.curBasicBlock);
            }
        }
        this.curBasicBlock = forStmt2Block;
        if (forStmt2 != null) {
            this.visitNodeForStmt(forStmt2);
        }
        IRBuildFactory.buildTermiBr(condBlock, this.curBasicBlock);
        this.curContinueBlock = continueBasicBlock;
        this.curBreakBlock = breakBasicBlock;
        this.curBasicBlock = nextBlock;
    }

    private void visitStmtBr(NodeStmtBr stmtBr) {   //'break' ';' | 'continue' ';'
        wordTuple type = stmtBr.getType();
        if (type.isBreak()) {
            if (this.curBreakBlock != null) {
                IRBuildFactory.buildTermiBr(this.curBreakBlock, this.curBasicBlock);
            }
        } else if (type.isContinue()) {
            if (this.curContinueBlock != null) {
                IRBuildFactory.buildTermiBr(this.curContinueBlock, this.curBasicBlock);
            }
        }
    }

    private void visitNodeStmtRe(NodeStmtRe stmtRe) {   // 'return' [Exp] ';' // 1.有Exp 2.无Exp
        NodeExp exp = stmtRe.getExp();
        if (exp != null) {
            this.visitNodeExp(exp);
            this.curVal = IRBuildFactory.buildTermiRet(this.curVal, this.curBasicBlock);
        } else {
            IRBuildFactory.buildTermiRet(this.curBasicBlock);
        }
    }

    private void visitNodeStmtGe(NodeStmtGe stmtGe) {   //LVal '=' 'getint''('')'';'
        NodeLVal lVal = stmtGe.getlVal();
        this.visitNodeLVal(lVal);
        Value left = this.curVal;
        String name = "getint";
        Function function = Module.getInstance().getFunctionByName("@" + name);
        this.curVal = IRBuildFactory.buildTermiCall(function, new ArrayList<>(), this.curBasicBlock);
        Value right = this.curVal;
        IRBuildFactory.buildMemStore(right, left, this.curBasicBlock);
    }

    private void visitNodeStmtPr(NodeStmtPr stmtPr) {   //'printf''('FormatString{','Exp}')'';'
        NodeFormatString formatString = stmtPr.getFormatString();
        ArrayList<NodeExp> exps = stmtPr.getExp();
        wordTuple str = formatString.getFormatString();
        int cnt = 0;
        ArrayList<Value> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder(str.getWordName().substring(1, str.getWordName().length() - 1));
        for (int i = 0; i < sb.length(); i++) {
            if (i != sb.length() - 1) {
                if (sb.charAt(i) == '%' && sb.charAt(i + 1) == 'd') {
                    if (cnt < exps.size()) {
                        this.visitNodeExp(exps.get(cnt));
                        values.add(this.curVal);
                        i++;
                        cnt++;
                    }
                }
            }
        }
        cnt = 0;
        for (int i = 0;i < sb.length();i++) {
            if (i == sb.length() - 1) {
                Function calledFunction = Module.getInstance().getFunctionByName("@putch");
                ArrayList<Value> args = new ArrayList<>();
                args.add(new ConstInt(32, sb.charAt(i)));
                this.curVal = IRBuildFactory.buildTermiCallVoid(calledFunction, args, this.curBasicBlock);
            } else {
                if (sb.charAt(i) == '\\' && sb.charAt(i + 1) == 'n') {
                    Function calledFunction = Module.getInstance().getFunctionByName("@putch");
                    ArrayList<Value> args = new ArrayList<>();
                    args.add(new ConstInt(32, 10));
                    this.curVal = IRBuildFactory.buildTermiCallVoid(calledFunction, args, this.curBasicBlock);
                    i++;            //额外加1
                } else if (sb.charAt(i) == '%' && sb.charAt(i + 1) == 'd' ) {
                    if (cnt < values.size()) {
                        Function calledFunction = Module.getInstance().getFunctionByName("@putint");
                        ArrayList<Value> args = new ArrayList<>();
                        args.add(values.get(cnt++));
                        this.curVal = IRBuildFactory.buildTermiCallVoid(calledFunction, args, this.curBasicBlock);
                    }
                    i++;            //额外加1
                } else {
                    Function calledFunction = Module.getInstance().getFunctionByName("@putch");
                    ArrayList<Value> args = new ArrayList<>();
                    args.add(new ConstInt(32, sb.charAt(i)));
                    this.curVal = IRBuildFactory.buildTermiCallVoid(calledFunction, args, this.curBasicBlock);
                }
            }
        }
    }

    private void visitNodeForStmt(NodeForStmt forStmt) {    //语句 ForStmt→ LVal '=' Exp // 存在即可
        NodeLVal lVal = forStmt.getlVal();
        NodeExp exp = forStmt.getExp();
        this.visitNodeLVal(lVal);
        Value left = this.curVal;
        this.visitNodeExp(exp);
        Value right = this.curVal;
        IRBuildFactory.buildMemStore(right, left, this.curBasicBlock);
    }

    private void visitNodeExp(NodeExp exp) {                //表达式 Exp → AddExp 注：SysY 表达式是int 型表达式 // 存在即可
        NodeAddExp addExp = exp.getAddExp();
        this.visitNodeAddExp(addExp);
    }

    private void visitNodeCond(NodeCond cond) {             //条件表达式 Cond → LOrExp // 存在即可
        NodeLOrExp lOrExp = cond.getlOrExp();
        this.visitNodeLOrExp(lOrExp, true);
    }

    private void visitNodeLVal(NodeLVal lVal) {             //左值表达式 LVal → Ident {'[' Exp ']'} //1.普通变量 2.一维数组 3.二维数组
        wordTuple ident = lVal.getIdent();
        String name = ident.getWordName();
        ArrayList<NodeExp> exps = lVal.getNodeExps();
        Value value = this.getSymbolVal(name);
        //if (((TypeAddr)value.getType()).getSrcType() instanceof TypeInt) {//
        if (value == null) {
            return;
        }
        if (value.getType() instanceof TypeInt) {
            if (value instanceof  GlobalVar) {
                this.curInt = ((ConstInt)((GlobalVar)value).getValue(0)).getVal();
            }
            //this.curInt = ((ConstInt)value).getVal();
            //this.curVal = IRBuildFactory.buildMemLoad(new TypeInt(32), value, this.curBasicBlock);
            this.curVal = value;
        } else if (value.getType() instanceof TypeArray) {
            TypeArray type = ((TypeArray) value.getType());
            for (int i = 0; i < exps.size(); i++) {
                this.visitNodeExp(exps.get(i));
                value = IRBuildFactory.buildMemGetelementptr(type, value, new ConstInt(32, 0), this.curVal, this.curBasicBlock);
                if (type.getTypeOfElement() instanceof TypeArray) {
                    type = ((TypeArray) type.getTypeOfElement());
                    if (i == exps.size() - 1) {
                        value = IRBuildFactory.buildMemGetelementptr(type, value, new ConstInt(32, 0), new ConstInt(32, 0), this.curBasicBlock);
                    }
                }
                this.curVal = value;
            }
        } else if (value.getType() instanceof TypeAddr) {
            Type type = ((TypeAddr) value.getType()).getSrcType();
            if (type instanceof TypeInt) {
                if (value instanceof  GlobalVar) {
                    this.curInt = ((ConstInt)((GlobalVar)value).getValue(0)).getVal();
                }
                this.curVal = value;
            } else if (type instanceof  TypeArray) {
                TypeArray typeArray = (TypeArray) type;
                if (this.isConst || this.isInit) {
                    if (!exps.isEmpty()) {
                        value = ((GlobalVar) value).getVal();
                        for (NodeExp exp : exps) {
                            this.visitNodeExp(exp);
                            value = (((ConstArray) value).getValue(this.curInt));
                        }
                    }
                } else {
                    if (exps.isEmpty()) {
                        value = IRBuildFactory.buildMemGetelementptr(typeArray, value, new ConstInt(32, 0), new ConstInt(32, 0), this.curBasicBlock);
                    } else {
                        for (int i = 0; i < exps.size(); i++) {
                            this.visitNodeExp(exps.get(i));
                            value = IRBuildFactory.buildMemGetelementptr(typeArray, value, new ConstInt(32, 0), this.curVal, this.curBasicBlock);
                            if (typeArray.getTypeOfElement() instanceof TypeArray) {
                                typeArray = ((TypeArray) typeArray.getTypeOfElement());
                                if (i == exps.size() - 1) {
                                    value = IRBuildFactory.buildMemGetelementptr(typeArray, value, new ConstInt(32, 0), new ConstInt(32, 0), this.curBasicBlock);
                                }
                            }
                        }
                    }
                }
                this.curVal = value;
            } else {
                TypeAddr typeAddr = ((TypeAddr) type);
                value= IRBuildFactory.buildMemLoad(typeAddr, value,  this.curBasicBlock);
                TypeArray typeArray = null;
                if (!exps.isEmpty()) {
                    this.visitNodeExp(exps.get(0));
                    value = IRBuildFactory.buildMemGetelementptr(typeAddr, value, this.curVal, this.curBasicBlock);
                    if (typeAddr.getSrcType() instanceof TypeArray) {
                        typeArray = (TypeArray) (typeAddr.getSrcType());
                        if (exps.size() == 1) {
                            value = IRBuildFactory.buildMemGetelementptr(typeArray, value, new ConstInt(32, 0), new ConstInt(32, 0), this.curBasicBlock);
                        }
                    }
                    for (int i = 1; i < exps.size(); i++) {
                        this.visitNodeExp(exps.get(i));
                        value = IRBuildFactory.buildMemGetelementptr(typeArray, value, new ConstInt(32, 0), this.curVal, this.curBasicBlock);
                        if (typeArray.getTypeOfElement() instanceof TypeArray) {
                            typeArray = ((TypeArray) typeArray.getTypeOfElement());
                            if (i == exps.size() - 1) {
                                value = IRBuildFactory.buildMemGetelementptr(typeArray, value, new ConstInt(32, 0), new ConstInt(32, 0), this.curBasicBlock);
                            }
                        }
                    }
                }
                this.curVal = value;
            }
        }
    }

    private void visitNodePrimaryExp(NodePrimaryExp primaryExp) {    //基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number
        NodeExp exp = primaryExp.getExp();
        NodeLVal lVal = primaryExp.getlVal();
        NodeNumber number = primaryExp.getNumber();
        if (exp != null) {
            this.visitNodeExp(exp);
        } else if (lVal != null) {
            boolean callingFunc = this.isCall;
            if (this.isCall) {
                this.isCall = false;
            }
            this.visitNodeLVal(lVal);
            if (!callingFunc) {
                if (this.curBasicBlock != null/*!(this.curVal instanceof GlobalVar)*/) {
                    if (this.curVal != null && ((TypeAddr) this.curVal.getType()).getSrcType() instanceof TypeInt) {
                        this.curVal = IRBuildFactory.buildMemLoad(new TypeInt(32), this.curVal, this.curBasicBlock);
                    }
                }
            }
            //if (this.isConst) {
            //    this.visitNodeLVal(lVal);
            //    this.curInt = ((ConstInt) curVal).getVal();
            //}
        } else if (number != null) {
            this.visitNodeNumber(number);
        }
    }

    private void visitNodeNumber(NodeNumber number) {           //数值 Number → IntConst // 存在即可
        this.curInt = Integer.parseInt(number.getIntConst().getWordName());
        if (!this.isConst) {
            this.curVal = new ConstInt(32, this.curInt);
        }
    }

    private void visitNodeUnaryExp(NodeUnaryExp unaryExp) {  //一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp
        NodePrimaryExp primaryExp = unaryExp.getPrimaryExp();
        wordTuple ident = unaryExp.getIdent();
        NodeFuncRParams funcRParams = unaryExp.getFuncRParams();
        NodeUnaryOp unaryOp = unaryExp.getUnaryOp();
        NodeUnaryExp nodeUnaryExp = unaryExp.getUnaryExp();
        if (this.isConst) {
            if (primaryExp != null) {
                this.visitNodePrimaryExp(primaryExp);
            } else {
                this.visitNodeUnaryExp(nodeUnaryExp);
                if (unaryOp.getOperator().isMinu()) {
                    this.curInt = this.curInt * (-1);
                } else if (unaryOp.getOperator().isNot()) {
                    if (this.curInt != 0) {
                        this.curInt = 0;
                    } else {
                        this.curInt = 1;
                    }
                }
            }
        } else {
            if (primaryExp != null) {
                this.visitNodePrimaryExp(primaryExp);
            } else if (ident != null) {
                String name = ident.getWordName();
                Function calledFunction = Module.getInstance().getFunctionByName("@" + name);
                int cnt = 0;
                ArrayList<Value> values = new ArrayList<>();
                ArrayList<Type> types = ((TypeFunction) calledFunction.getType()).getParams();
                if (funcRParams != null) {  //函数实参表 FuncRParams → Exp { ',' Exp } // 1.花括号内重复0次 2.花括号内重复多次 3.Exp需要覆盖数组传参和部分数组传参
                    for (NodeExp exp : funcRParams.getExp()) {
                        if (cnt < types.size()) {
                            if (!(types.get(cnt) instanceof TypeInt)) {
                                this.isCall = true;
                            }
                            this.visitNodeExp(exp);
                            this.isCall = false;
                            values.add(this.curVal);
                            cnt++;
                        }
                    }
                }
                if (calledFunction.getReturnType() instanceof TypeVoid) {
                    this.curVal = IRBuildFactory.buildTermiCallVoid(calledFunction, values, this.curBasicBlock);
                } else {
                    this.curVal = IRBuildFactory.buildTermiCall(calledFunction, values, this.curBasicBlock);
                }
            } else if (nodeUnaryExp != null) {
                this.visitNodeUnaryExp(nodeUnaryExp);
                if (unaryOp.getOperator().isMinu()) {
                    this.curVal = IRBuildFactory.buildBinSub(new ConstInt(32, 0), this.curVal, this.curBasicBlock);
                } else if (unaryOp.getOperator().isNot()) {
                    if (((TypeInt)this.curVal.getType()).getBits() < 32) {
                        this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), this.curVal, this.curBasicBlock);
                    }
                    this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.EQ, new ConstInt(32, 0), this.curVal, this.curBasicBlock);
                }
            }
        }
    }

    private void visitNodeMulExp(NodeMulExp mulExp) {    //乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp // 1.UnaryExp 2.* 3./ 4.% 均需覆盖
        NodeUnaryExp unaryExp1 = mulExp.getUnaryExp1();
        NodeMulExp nodeMulExp = mulExp.getMulExp();
        wordTuple operator = mulExp.getOperator();
        NodeUnaryExp unaryExp2 = mulExp.getUnaryExp2();
        if (this.isConst) {
            if (unaryExp1 != null) {
                this.visitNodeUnaryExp(unaryExp1);
            } else {
                this.visitNodeMulExp(nodeMulExp);
                int left= this.curInt;
                this.visitNodeUnaryExp(unaryExp2);
                if (operator.isMult()) {
                    curInt = left * curInt;
                } else if (operator.isDiv()) {
                    curInt = left / curInt;
                } else if (operator.isMod()){
                    curInt = left % curInt;
                }
            }
        } else {
            if (unaryExp1 != null) {
                this.visitNodeUnaryExp(unaryExp1);
            } else {
                this.visitNodeMulExp(nodeMulExp);
                Value left = this.curVal;
                this.visitNodeUnaryExp(unaryExp2);
                if (operator.isMult()) {
                    this.curVal = IRBuildFactory.buildBinMul(left, this.curVal, this.curBasicBlock);
                } else if (operator.isDiv()) {
                    this.curVal = IRBuildFactory.buildBinSdiv(left, this.curVal, this.curBasicBlock);
                } else if (operator.isMod()){
                    this.curVal = IRBuildFactory.buildBinSrem(left, this.curVal, this.curBasicBlock);
                }
            }
        }
    }

    private void visitNodeAddExp(NodeAddExp addExp) {   //加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp // 1.MulExp 2.+ 需覆盖 3.- 需覆盖
        NodeMulExp mulExp1 = addExp.getMulExp1();
        NodeAddExp nodeAddExp = addExp.getAddExp();
        wordTuple operator = addExp.getOperator();
        NodeMulExp mulExp2 = addExp.getMulExp2();
        if (this.isConst) {
            if (mulExp1 != null) {
                this.visitNodeMulExp(mulExp1);
            } else {
                this.visitNodeAddExp(nodeAddExp);
                int left = this.curInt;
                this.visitNodeMulExp(mulExp2);
                if (operator.isPlus()) {
                    this.curInt = left + this.curInt;
                } else if (operator.isMinu()) {
                    this.curInt = left - this.curInt;
                }
            }
        } else {
            if (mulExp1 != null) {
                this.visitNodeMulExp(mulExp1);
            } else {
                this.visitNodeAddExp(nodeAddExp);
                Value left = this.curVal;
                this.visitNodeMulExp(mulExp2);
                if (operator.isPlus()) {
                   this.curVal = IRBuildFactory.bulidBinAdd(left, this.curVal, this.curBasicBlock);
                } else if (operator.isMinu()) {
                   this.curVal = IRBuildFactory.buildBinSub(left, this.curVal, this.curBasicBlock);
                }
            }
        }
    }

    private void visitNodeRelExp(NodeRelExp relExp, boolean flag) {   //关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp // 1.AddExp2.< 3.> 4.<= 5.>= 均需覆盖
        NodeAddExp addExp1 = relExp.getAddExp1();
        NodeRelExp nodeRelExp = relExp.getRelExp();
        wordTuple operator = relExp.getOperator();
        NodeAddExp addExp2 = relExp.getAddExp2();
        if (addExp1 != null) {
            if (!flag) {
                this.isCruit = false;
            }
            this.visitNodeAddExp(addExp1);
        } else {
            this.visitNodeRelExp(nodeRelExp, false);
            Value left = this.curVal;
            if (((TypeInt)left.getType()).getBits() < 32) {
                this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), left, this.curBasicBlock);
            }
            left = this.curVal;
            this.visitNodeAddExp(addExp2);
            if (((TypeInt)this.curVal.getType()).getBits() < 32) {
                this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), this.curVal, this.curBasicBlock);
            }
            if (operator.isLss()) {
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.SLT, left, this.curVal, this.curBasicBlock);
            } else if (operator.isGre()) {
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.SGT, left, this.curVal, this.curBasicBlock);
            } else if (operator.isLeq()) {
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.SLE, left, this.curVal, this.curBasicBlock);
            } else if (operator.isGeq()){
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.SGE, left, this.curVal, this.curBasicBlock);
            }
        }
    }

    private void visitNodeEqExp(NodeEqExp eqExp, boolean flag) { //相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp // 1.RelExp 2.== 3.!= 均需覆盖
        NodeRelExp relExp1 = eqExp.getRelExp1();
        NodeEqExp nodeEqExp = eqExp.getEqExp();
        wordTuple operator = eqExp.getOperator();
        NodeRelExp relExp2 = eqExp.getRelExp2();
        if (relExp1 != null) {
            if (!flag) {
                this.isCruit = false;
            }
            this.visitNodeRelExp(relExp1, true);
        } else {
            this.visitNodeEqExp(nodeEqExp, false);
            Value left = this.curVal;
            if (((TypeInt)left.getType()).getBits() < 32) {
                this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), left, this.curBasicBlock);
            }
            left = this.curVal;
            this.visitNodeRelExp(relExp2, true);
            if (((TypeInt)this.curVal.getType()).getBits() < 32) {
                this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), this.curVal, this.curBasicBlock);
            }
            if (operator.isEql()) {
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.EQ, left, this.curVal, this.curBasicBlock);
            } else {  // NE
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.NE, left, this.curVal, this.curBasicBlock);
            }
        }
        /*
        if (relExp1 != null) {
            this.visitRelExp(relExp1);
            if (((TypeInt) this.curVal.getType()).getBits() < 32) {
                this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), this.curVal, this.curBasicBlock);
            }
            this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.NE, this.curVal, new ConstInt(32, 0), this.curBasicBlock);
        } else {
            this.visitEqExp(nodeEqExp);
            Value left = this.curVal;
            if (((TypeInt)left.getType()).getBits() < 32) {
                this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), left, this.curBasicBlock);
            }
            this.visitRelExp(relExp2);
            if (operator.isEql()) {
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.EQ, left, this.curVal, this.curBasicBlock);
            } else if (operator.isNeq()) {
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.NE, left, this.curVal, this.curBasicBlock);
            }
        }
        */
    }

    private void visitNodeLAndExp(NodeLAndExp lAndExp, boolean flag) { //逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp // 1.EqExp 2.&& 均需覆盖
        NodeEqExp eqExp1 = lAndExp.getEqExp1();
        NodeLAndExp nodeLAndExp = lAndExp.getlAndExp();
        NodeEqExp eqExp2 = lAndExp.getEqExp2();
        BasicBlock ifBlock = this.curIfBlock;
        BasicBlock elseBlock = this.curElseBlock;
        BasicBlock nextAndBlock = this.curNextAndBlock;
        BasicBlock nextOrBlock = this.curNextOrBlock;
        if (eqExp1 != null) {
            if (flag) {
                this.isCruit = true;
                this.visitNodeEqExp(eqExp1, true);
                if (this.isCruit) {
                    if (((TypeInt)this.curVal.getType()).getBits() < 32) {
                        this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), this.curVal, this.curBasicBlock);
                    }
                    this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.NE, this.curVal, new ConstInt(32, 0), this.curBasicBlock);
                    isCruit = false;
                }
                IRBuildFactory.buildTermiBr(this.curVal, ifBlock, elseBlock, this.curBasicBlock);
                this.curBasicBlock = ifBlock;
            } else {
                BasicBlock nextBasicBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
                this.isCruit = true;
                this.visitNodeEqExp(eqExp1, true);
                if (this.isCruit) {
                    if (((TypeInt)this.curVal.getType()).getBits() < 32) {
                        this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), this.curVal, this.curBasicBlock);
                    }
                    this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.NE, this.curVal, new ConstInt(32, 0), this.curBasicBlock);
                    this.isCruit = false;
                }
                IRBuildFactory.buildTermiBr(this.curVal, nextBasicBlock, elseBlock, this.curBasicBlock);
                this.curBasicBlock = nextBasicBlock;
            }
        } else {
            this.visitNodeLAndExp(nodeLAndExp, false);
            BasicBlock nextBasicBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
            this.isCruit = true;
            this.visitNodeEqExp(eqExp2, true);
            if (this.isCruit) {
                if (((TypeInt)this.curVal.getType()).getBits() < 32) {
                    this.curVal = IRBuildFactory.buildZextTo(new TypeInt(32), this.curVal, this.curBasicBlock);
                }
                this.curVal = IRBuildFactory.buildBinIcmp(BinIcmpType.NE, this.curVal, new ConstInt(32, 0), this.curBasicBlock);
                this.isCruit = false;
            }
            IRBuildFactory.buildTermiBr(this.curVal, nextBasicBlock, elseBlock, this.curBasicBlock);
            this.curBasicBlock = nextBasicBlock;
            if (flag) {
                IRBuildFactory.buildTermiBr(ifBlock, this.curBasicBlock);
            }
        }
        /*
        if (eqExp1 != null) {
            if (!last) {    //为真则跳转下一个与表达式，否则跳转到下一个或表达式
                this.visitEqExp(eqExp1);
                IRBuildFactory.buildTermiBr(this.curVal, nextAndBlock, nextOrBlock, this.curBasicBlock);
                this.curBasicBlock = nextAndBlock;
            } else {        //为真则跳转ifBlock，否则跳转到elseBlock
                this.visitEqExp(eqExp1);
                //IRBuildFactory.buildTermiBr(this.curVal, ifBlock, elseBlock, this.curBasicBlock);
                //this.curBasicBlock = elseBlock;
            }
        } else {
            this.curNextAndBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
            this.visitLAndExp(nodeLAndExp, false);
            this.curNextAndBlock = nextAndBlock;
            this.visitEqExp(eqExp2);
        }
        */
        this.curIfBlock = ifBlock;
        this.curElseBlock = elseBlock;
        this.curNextOrBlock = nextOrBlock;
        this.curNextAndBlock = nextAndBlock;
    }

    private void visitNodeLOrExp(NodeLOrExp lOrExp, boolean flag) { //逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp // 1.LAndExp 2.|| 均需覆盖
        NodeLAndExp lAndExp1 = lOrExp.getlAndExp1();
        NodeLOrExp nodelOrExp = lOrExp.getlOrExp();
        NodeLAndExp lAndExp2 = lOrExp.getlAndExp2();
        BasicBlock ifBlock = this.curIfBlock;
        BasicBlock elseBlock = this.curElseBlock;
        BasicBlock nextAndBlock = this.curNextAndBlock;
        BasicBlock reBlock = this.curNextOrBlock;;
        BasicBlock nextOrBlock = this.curNextOrBlock;
        if (lAndExp1 != null) {
            if (flag) {
                this.visitNodeLAndExp(lAndExp1, true);
            } else {
                BasicBlock nextBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
                this.curElseBlock = nextBlock;
                this.visitNodeLAndExp(lAndExp1, true);
                this.curBasicBlock = nextBlock;
            }
        } else {
            this.visitNodeLOrExp(nodelOrExp, false);
            if (flag) {
                this.visitNodeLAndExp(lAndExp2, true);
            } else {
                BasicBlock nextBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
                this.curElseBlock = nextBlock;
                this.visitNodeLAndExp(lAndExp2, true);
                this.curBasicBlock = nextBlock;
            }
        }
        /*
        if (last && this.curNextOrBlock == null) {
            nextOrBlock = elseBlock;
            this.curNextOrBlock = nextOrBlock;
        }
        if (lAndExp1 != null) {
            if (!last) {    //连或的不是最后一个LAndExp，若为真直接跳转至ifBlock，否则跳转下一个lAndExp
                this.visitLAndExp(lAndExp1, true);
                IRBuildFactory.buildTermiBr(this.curVal, ifBlock, nextOrBlock, this.curBasicBlock);
                this.curBasicBlock = nextOrBlock;
            } else {        //连或的最后一个LAndExp，若为真直接跳转至ifBlock，否则跳转elseBlock
                this.visitLAndExp(lAndExp1, true);
                IRBuildFactory.buildTermiBr(this.curVal, ifBlock, elseBlock, this.curBasicBlock);
                this.curBasicBlock = nextOrBlock;//elseBlock;
            }
        } else {
            this.curNextOrBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
            this.visitLOrExp(nodelOrExp, false);
            this.curNextOrBlock = nextOrBlock;
            this.visitLAndExp(lAndExp2, true);
            if (last) { //后面没有更多的‘||’了
                IRBuildFactory.buildTermiBr(this.curVal, ifBlock, elseBlock, this.curBasicBlock);
                this.curBasicBlock = ifBlock;
            } else {
                IRBuildFactory.buildTermiBr(this.curVal, ifBlock, nextOrBlock, this.curBasicBlock);
                this.curBasicBlock = nextOrBlock;
            }
        }
        */
        this.curIfBlock = ifBlock;
        this.curElseBlock = elseBlock;
        this.curNextAndBlock = nextAndBlock;
        this.curNextOrBlock = reBlock;
    }

    private void visitNodeConstExp(NodeConstExp constExp) { //常量表达式 ConstExp → AddExp
        NodeAddExp addExp = constExp.getAddExp();
        this.visitNodeAddExp(addExp);
        if (this.isConst) {
            this.curVal = new ConstInt(32, this.curInt);
        }
    }
}
