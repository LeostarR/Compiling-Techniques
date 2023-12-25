package GrammaticalTools;

import GrammaticalTools.GrammaticalTreeDef.*;
import LexicalTools.wordTuple;
import SymbolTools.IdentType;
import SymbolTools.Symbol;
import SymbolTools.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private final ArrayList<wordTuple> wordList;
    private int index;
    private wordTuple token;
    private int lineNum;
    private final NodeCompUnit compUnit;
    private final boolean printLG;
    private boolean printGrammarInf;
    private boolean printLexicalInf;
    private final boolean printE;
    private boolean printErrorInf;
    private final ArrayList<SymbolTable> tables;
    private int tableIndex = 0;
    private int curIndex = -1;
    private final HashMap<Integer, IdentType> typeMap = new HashMap<>();

    public Parser(ArrayList<wordTuple> wordList) {
        this.wordList = wordList;
        this.printLG = false;
        this.printGrammarInf = false;
        this.printLexicalInf = false;
        this.printE = true;
        this.printErrorInf = true;
        this.index = -1;
        this.token = null;
        this.lineNum = 0;
        this.next();
        this.tables = new ArrayList<>();
        this.createTable(null);
        this.typeMap.put(0, IdentType.COMMONVAR);
        this.typeMap.put(1, IdentType.ONEDIMARR);
        this.typeMap.put(2, IdentType.TWODIMARR);
        this.typeMap.put(-1, IdentType.FUNCTION);
        compUnit = parseCompUnit();
    }

    private void createTable(Symbol symbol) {   //symbol是函数名，最外层symbol为null
        SymbolTable preTable;
        if (this.curIndex == -1) {
            preTable = null;
        } else {
            preTable = this.getTables().get(this.curIndex);
        }
        SymbolTable table =  new SymbolTable(tableIndex++, this.curIndex, symbol, preTable);
        if (preTable != null) {
            preTable.addNextTable(table);
        }
        this.curIndex = this.tableIndex - 1;
        this.tables.add(table);
    }

    private void createFuncTable(wordTuple ident, boolean isVoid) {
        IdentType type = IdentType.FUNCTION;
        SymbolTable oldTable = this.tables.get(this.curIndex);
        Symbol symbol = new Symbol(this.wordList.indexOf(ident), this.curIndex, ident, type, isVoid, 0);
        oldTable.addFunc(ident.getWordName(), symbol);
        createTable(symbol);
    }

    public ArrayList<SymbolTable> getTables() {
        return tables;
    }

    public NodeCompUnit getCompUnit() {
        return this.compUnit;
    }

    private void next() {
        this.index++;
        if (index >= wordList.size()) {
            return;
        }
        this.token = wordList.get(index);
        this.lineNum = this.token.getWordLine();
    }

    private void defNewVar(wordTuple ident, IdentType identType, boolean isConst) {
        SymbolTable curTable = this.tables.get(this.curIndex);
        if (curTable.containsVar(ident.getWordName()) || curTable.containsFunc(ident.getWordName())) {    //重复定义，报错
            this.checkErrorB(ident.getWordLine());
        } else {    //否则，填表
            Symbol symbol = new Symbol(this.wordList.indexOf(ident), this.curIndex, ident, identType, isConst);
            curTable.addVar(ident.getWordName(), symbol);
        }
    }

    private boolean defNewFunc(wordTuple ident, boolean isVoid) {
        SymbolTable curTable = this.tables.get(this.curIndex);
        if (curTable.containsFunc(ident.getWordName()) || curTable.containsVar(ident.getWordName())) {   //重复定义，报错
            this.checkErrorB(ident.getWordLine());
            return false;
        } else {    //否则，填表并建表
            this.createFuncTable(ident, isVoid);
            return true;
        }
    }

    private void checkErrorA(int lineNum) {
        if (this.printErrorInf) {
            System.out.println(lineNum + " a");
        }
    }

    private void checkErrorB(int lineNum) {
        if (this.printErrorInf) {
            System.out.println(lineNum + " b");
        }
    }

    private void checkErrorC(wordTuple ident, boolean isFunc) {
        if (isFunc) {
            if (this.findFunc(ident.getWordName()) == null) {
                if (this.printErrorInf) {
                    System.out.println(ident.getWordLine() + " c");
                }
            }
        } else {
            if (this.findVar(ident.getWordName()) == null) {
                if (this.printErrorInf) {
                    System.out.println(ident.getWordLine() + " c");
                }
            }
        }
    }

    private boolean checkErrorD(wordTuple ident, int rSize) {
        Symbol symbol = this.findFunc(ident.getWordName());
        if (symbol != null && rSize != symbol.getParamNum()) {
            if (this.printErrorInf) {
                System.out.println(ident.getWordLine() + " d");
            }
            return false;
        }
        return true;
    }

    private void checkErrorE(wordTuple ident, ArrayList<IdentType> typeList) {
        if (this.printErrorInf) {
            Symbol symbol = this.findFunc(ident.getWordName());
            if (symbol != null && !symbol.getParamTypeList().equals(typeList)) {
                System.out.println(ident.getWordLine() + " e");
            }
        }
    }

    private void checkErrorF(int lineNum) {
        if (this.printErrorInf) {
            SymbolTable curTable = this.tables.get(this.curIndex);
            if (curTable.isVoid()) { //void不该出现返回值但是返回了一个Exp
                System.out.println(lineNum + " f");
            }
        }
    }

    private void checkErrorG(NodeBlock block) {
        int lineNum = this.wordList.get(index - 1).getWordLine();
        ArrayList<NodeBlockItem> blockItems = block.getBlockItems();
        if (this.printErrorInf) {
            if (blockItems.isEmpty()) {
                System.out.println(lineNum + " g");
            } else {
                NodeBlockItem item = blockItems.get(blockItems.size() - 1);
                if (item.getDecl() != null) {
                    System.out.println(lineNum + " g");
                } else if (item.getStmt() != null && !(item.getStmt() instanceof NodeStmtRe)) {
                    this.checkStmtG(item.getStmt(), lineNum);
                }
                /*
                if (!item.isReturnStmt()) {
                    System.out.println(lineNum + " g");
                }*/
            }
        }
    }

    private void checkStmtG(NodeStmt stmt, int lineNum) {
        if (stmt instanceof NodeStmtBl) {
            this.checkErrorG(((NodeStmtBl) stmt).getBlock());
        } else if (stmt instanceof NodeStmtIf) {
            if (((NodeStmtIf) stmt).getStmtElse() == null) {
                System.out.println(lineNum + " g");
            } else {
                NodeStmt node = ((NodeStmtIf) stmt).getStmtElse();
                this.checkStmtG(node, lineNum);
            }
        } else if (stmt instanceof NodeStmtFo) {
            NodeStmt node = ((NodeStmtFo) stmt).getStmt();
            this.checkStmtG(node, lineNum);
        } else if (!(stmt instanceof NodeStmtRe)) {
            System.out.println(lineNum + " g");
        }
    }

    private void checkErrorH(NodeLVal lVal) {
        if (this.printErrorInf) {
            wordTuple ident = lVal.getIdent();
            Symbol symbol = this.findVar(ident.getWordName());
            if (symbol != null && symbol.isConst()) {
                System.out.println(lVal.getLineNum() + " h");
            }
        }
    }

    private void checkErrorI(int lineNum) {
        if (this.printErrorInf) {
            System.out.println(lineNum + " i");
        }
    }

    private void checkErrorJ(int lineNum) {
        if (this.printErrorInf) {
            System.out.println(lineNum + " j");
        }
    }

    private void checkErrorK(int lineNum) {
        if (this.printErrorInf) {
            System.out.println(lineNum + " k");
        }
    }

    private void checkErrorL(int lineNum) {
        if (this.printErrorInf) {
            System.out.println(lineNum + " l");
        }
    }

    private void checkErrorM(int lineNum) {
        if (this.printErrorInf) {
            System.out.println(lineNum + " m");
        }
    }

    private Symbol findVar(String key) {
        int id = curIndex;
        while (id != -1) {
            SymbolTable table = this.tables.get(id);
            if (table.containsVar(key)) {
                return table.getVar(key);
            }
            id = table.getFatherId();
        }
        return null;
    }

    private Symbol findFunc(String key) {
        int id = curIndex;
        while (id != -1) {
            SymbolTable table = this.tables.get(id);
            if (table.containsFunc(key) ) {
                return table.getFunc(key);
            }
            id = table.getFatherId();
        }
        return null;
    }

    private NodeCompUnit parseCompUnit() {
        ArrayList<NodeDecl> decls = new ArrayList<>();
        ArrayList<NodeFuncDef> funcDefs = new ArrayList<>();
        while (this.isDecl()) {
            NodeDecl decl = this.parseDecl();
            decls.add(decl);
        }
        while (this.isFuncDef()) {
            NodeFuncDef funcDef = this.parseFuncDef();
            funcDefs.add(funcDef);
        }
        NodeMainFuncDef mainFuncDef = this.parseMainFuncDef();
        int line = this.lineNum;
        if (!decls.isEmpty()) {
            line = decls.get(0).getLineNum();
        }
        NodeCompUnit nodeCompUnit = new NodeCompUnit(line, decls, funcDefs, mainFuncDef);
        if (this.printGrammarInf) {
            System.out.println("<CompUnit>");
        }
        return nodeCompUnit;
    }

    private NodeDecl parseDecl() {  //Decl → ConstDecl | VarDecl
        if (this.isConstDecl()) {
            NodeConstDecl constDecl = this.parseConstDecl();
            return new NodeDecl(constDecl.getLineNum(), constDecl);
        } else {
            NodeVarDecl varDecl = this.parseVarDecl();
            return new NodeDecl(varDecl.getLineNum(), varDecl);
        }
    }

    private NodeConstDecl parseConstDecl() {    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        wordTuple constType = this.token;
        if (this.printLexicalInf) {
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        NodeBType bType = this.parseBType();
        ArrayList<NodeConstDef> constDefs = new ArrayList<>();
        NodeConstDef constDef = this.parseConstDef();
        constDefs.add(constDef);
        int lineNum = constDef.getLineNum();
        while (this.token.isComa()) {
            if (this.printLexicalInf) { //','
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            constDef = this.parseConstDef();
            constDefs.add(constDef);
            lineNum = constDef.getLineNum();
        }
        if (this.token.isSemi()) {  //';'
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        } else {
            this.checkErrorI(lineNum);
        }
        NodeConstDecl constDecl = new NodeConstDecl(constType.getWordLine(), constType, bType, constDefs);
        if (this.printGrammarInf) {
            System.out.println("<ConstDecl>");
        }
        return constDecl;
    }

    private NodeBType parseBType() {    //BType → 'int'
        wordTuple constant = this.token;
        NodeBType nodeBType = new NodeBType(this.lineNum, constant);
        if (this.printLexicalInf) {
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        return nodeBType;
    }

    private NodeConstDef parseConstDef() {  //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        wordTuple ident = this.token;
        ArrayList<NodeConstExp> constExps = new ArrayList<>();
        if (this.printLexicalInf) { //ident
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        while (this.token.isLbk()) {
            if (this.printLexicalInf) { //'['
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeConstExp constExp = this.parseConstExp();
            constExps.add(constExp);
            int lineNum = constExp.getLineNum();
            if (this.token.isRbk()) {   //']'
                if (this.printLexicalInf) {
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorK(lineNum);
            }
        }
        IdentType type = this.typeMap.get(constExps.size());
        this.defNewVar(ident, type, true);
        if (this.printLexicalInf) { //'='
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        NodeConstInitVal constInitVal = this.parseConstInitVal();
        NodeConstDef constDef = new NodeConstDef(ident.getWordLine(), ident, constExps, constInitVal);
        if (this.printGrammarInf) {
            System.out.println("<ConstDef>");
        }
        return constDef;
    }

    private NodeConstInitVal parseConstInitVal() {  //ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        NodeConstInitVal constInitVal;
        if (this.token.isLbe()) {   //'{' [ ConstInitVal { ',' ConstInitVal } ] '}'
            if (this.printLexicalInf) { //'{'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (!this.token.isRbe()) {
                ArrayList<NodeConstInitVal> constInitVals = new ArrayList<>();
                NodeConstInitVal initVal = this.parseConstInitVal();
                constInitVals.add(initVal);
                while (this.token.isComa()) {
                    if (this.printLexicalInf) { //','
                        System.out.println(token.getLexType() + " " + token.getWordName());
                    }
                    this.next();
                    initVal = this.parseConstInitVal();
                    constInitVals.add(initVal);
                }
                if (this.printLexicalInf) { //'}'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
                int line = this.lineNum;
                if (!constInitVals.isEmpty()) {
                    line = constInitVals.get(0).getLineNum();
                }
                constInitVal = new NodeConstInitVal(line, constInitVals);
            } else {
                constInitVal = new NodeConstInitVal(this.token.getWordLine());
                if (this.printLexicalInf) { //'}'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            }
        } else {
            NodeConstExp constExp = this.parseConstExp();
            constInitVal = new NodeConstInitVal(constExp.getLineNum(), constExp);
        }
        if (this.printGrammarInf) {
            System.out.println("<ConstInitVal>");
        }
        return constInitVal;
    }

    private NodeVarDecl parseVarDecl() {    //VarDecl → BType VarDef { ',' VarDef } ';'
        NodeBType bType = this.parseBType();
        ArrayList<NodeVarDef> varDefs = new ArrayList<>();
        NodeVarDef varDef = this.parseVarDef();
        varDefs.add(varDef);
        int lineNum = varDef.getLineNum();
        while (this.token.isComa()) {   //','
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            varDef = this.parseVarDef();
            varDefs.add(varDef);
            lineNum = varDef.getLineNum();
        }
        if (this.token.isSemi()) {
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        } else {
            this.checkErrorI(lineNum);
        }
        NodeVarDecl varDecl = new NodeVarDecl(bType.getLineNum(), bType, varDefs);
        if (this.printGrammarInf) {
            System.out.println("<VarDecl>");
        }
        return varDecl;
    }

    private NodeVarDef parseVarDef() {  //VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        NodeVarDef nodeVarDef;
        wordTuple ident = this.token;
        if (this.token.isIdent()) {
            if (this.printLexicalInf) { //'ident'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        }
        ArrayList<NodeConstExp> constExps = new ArrayList<>();
        while (this.token.isLbk()) {
            if (this.printLexicalInf) { //'['
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeConstExp constExp = this.parseConstExp();
            constExps.add(constExp);
            int lineNum = constExp.getLineNum();
            if (this.token.isRbk()) {
                if (this.printLexicalInf) { //']'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorK(lineNum);
            }
        }
        IdentType type = this.typeMap.get(constExps.size());
        this.defNewVar(ident, type, false);
        if (!this.token.isAssign()) {
            nodeVarDef = new NodeVarDef(ident.getWordLine(), ident, constExps);
        } else {
            if (this.printLexicalInf) { //'='
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeInitVal initVal = this.parseInitVal();
            nodeVarDef = new NodeVarDef(ident.getWordLine(), ident, constExps, initVal);
        }
        if (this.printGrammarInf) {
            System.out.println("<VarDef>");
        }
        return nodeVarDef;
    }

    private NodeInitVal parseInitVal() {    //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        NodeInitVal initVal;
        if (!this.token.isLbe()) {  //Exp
            NodeExp exp = this.parseExp(false);
            initVal = new NodeInitVal(exp.getLineNum(), exp);
        } else {    //'{' [ InitVal { ',' InitVal } ] '}'
            if (this.printLexicalInf) { //'{'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.token.isRbe()) {
                initVal = new NodeInitVal(this.lineNum);
                if (this.printLexicalInf) {
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                ArrayList<NodeInitVal> initVals = new ArrayList<>();
                initVal = this.parseInitVal();
                initVals.add(initVal);
                while (this.token.isComa()) {
                    if (this.printLexicalInf) { //','
                        System.out.println(token.getLexType() + " " + token.getWordName());
                    }
                    this.next();
                    initVal = this.parseInitVal();
                    initVals.add(initVal);
                }
                if (this.token.isRbe()) {
                    if (this.printLexicalInf) { //'}'
                        System.out.println(token.getLexType() + " " + token.getWordName());
                    }
                    this.next();
                }
                int line = this.lineNum;
                if (!initVals.isEmpty()) {
                    line = initVals.get(0).getLineNum();
                }
                initVal = new NodeInitVal(line, initVals);
            }
        }
        if (this.printGrammarInf) {
            System.out.println("<InitVal>");
        }
        return initVal;
    }

    private NodeFuncDef parseFuncDef() {    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        NodeFuncDef funcDef = null;
        NodeFuncType funcType = this.parseFuncType();
        wordTuple ident = this.token;
        if (this.token.isIdent()) {
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        }
        boolean isVoid = funcType.getType().isVoid();
        int preIndex = this.curIndex;
        boolean legitimate = this.defNewFunc(ident, isVoid);
        if (this.token.isLpt()) {
            if (this.printLexicalInf) { //'('
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.token.isRpt()) { //FuncType Ident '(' ')' Block
                if (this.printLexicalInf) { //')'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
                NodeBlock block = this.parseBlock(false);
                funcDef = new NodeFuncDef(funcType.getLineNum(), funcType, ident, block);
            } else if (this.isFuncFParams()){    //FuncType Ident '(' [FuncFParams] ')' Block
                NodeFuncFParams funcFParams = this.parseFuncFParams();
                if (legitimate) {   //成功定义
                    int paramNum = funcFParams.getParamNum();
                    SymbolTable table = this.tables.get(preIndex);
                    Symbol symbol = table.getFunc(ident.getWordName());
                    symbol.setParamNum(paramNum);
                    ArrayList<IdentType> typeList = funcFParams.getParamTypeList();
                    symbol.setTypeList(typeList);
                }
                if (this.token.isRpt()) {
                    if (this.printLexicalInf) { //')'
                        System.out.println(token.getLexType() + " " + token.getWordName());
                    }
                    this.next();
                } else {
                    this.checkErrorJ(funcFParams.getLineNum());
                }
                NodeBlock block = this.parseBlock(false);
                funcDef = new NodeFuncDef(funcType.getLineNum(), funcType, ident, funcFParams, block);
            } else {
                this.checkErrorJ(ident.getWordLine());
                NodeBlock block = this.parseBlock(false);
                funcDef = new NodeFuncDef(funcType.getLineNum(), funcType, ident, block);
            }
        }
        if (funcDef != null && funcType.getType().isInt()) {
            this.checkErrorG(funcDef.getBlock());
        }
        if (this.printGrammarInf) {
            System.out.println("<FuncDef>");
        }
        this.curIndex = preIndex;
        return funcDef;
    }

    private NodeMainFuncDef parseMainFuncDef() { //MainFuncDef → 'int' 'main' '(' ')' Block
        wordTuple constant = this.token;
        if (this.token.isInt()) {
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        }
        wordTuple funName = this.token;
        if (this.token.isMain()) {
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        }
        if (this.printLexicalInf) { //'('
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        if (this.token.isRpt()) {
            if (this.printLexicalInf) { //')'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        } else {
            this.checkErrorJ(funName.getWordLine());
        }
        int preIndex = this.curIndex;
        this.defNewFunc(funName, false);
        NodeBlock block = this.parseBlock(false);
        NodeMainFuncDef mainFuncDef = new NodeMainFuncDef(constant.getWordLine(), constant, funName, block);
        this.checkErrorG(block);
        if (this.printGrammarInf) {
            System.out.println("<MainFuncDef>");
        }
        this.curIndex = preIndex;
        return mainFuncDef;
    }

    private NodeFuncType parseFuncType() {  //FuncType → 'void' | 'int'
        wordTuple type = this.token;
        if (this.token.isInt() || this.token.isVoid()) {
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
        }
        NodeFuncType funcType = new NodeFuncType(type.getWordLine(), type);
        if (this.printGrammarInf) {
            System.out.println("<FuncType>");
        }
        return funcType;
    }

    private NodeFuncFParams parseFuncFParams() {    //FuncFParams → FuncFParam { ',' FuncFParam }
        ArrayList<NodeFuncFParam> funcFParams = new ArrayList<>();
        NodeFuncFParam funcFParam = this.parseFuncFParam();
        funcFParams.add(funcFParam);
        while (this.token.isComa()) {
            if (this.printLexicalInf) { //','
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            funcFParam = this.parseFuncFParam();
            funcFParams.add(funcFParam);
        }
        int line = this.lineNum;
        if (!funcFParams.isEmpty()) {
            line = funcFParams.get(0).getLineNum();
        }
        NodeFuncFParams nodeFuncFParams = new NodeFuncFParams(line, funcFParams);
        if (this.printGrammarInf) {
            System.out.println("<FuncFParams>");
        }
        return nodeFuncFParams;
    }

    private NodeFuncFParam parseFuncFParam() {  //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        NodeFuncFParam funcFParam;
        NodeBType bType = this.parseBType();
        wordTuple ident = this.token;
        int lineNum = ident.getWordLine();
        int dimension = 0;
        if (this.printLexicalInf) {
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        if (this.token.isLbk()) {   //'['
            if (this.printLexicalInf) {
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.token.isRbk()) {
                if (this.printLexicalInf) { //']'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorK(lineNum);
            }
            ArrayList<NodeConstExp> constExps = new ArrayList<>();
            while (this.token.isLbk()) {
                if (this.printLexicalInf) { //'['
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
                NodeConstExp constExp = this.parseConstExp();
                constExps.add(constExp);
                lineNum = constExp.getLineNum();
                if (this.token.isRbk()) {
                    if (this.printLexicalInf) { //']'
                        System.out.println(token.getLexType() + " " + token.getWordName());
                    }
                    this.next();
                } else {
                    this.checkErrorK(lineNum);
                }
            }
            if (constExps.size() == 1) {
                dimension = 2;
            } else {
                dimension = 1;
            }
            funcFParam = new NodeFuncFParam(bType.getLineNum(), bType, ident, constExps);
        } else {
            funcFParam = new NodeFuncFParam(bType.getLineNum(), bType, ident);
        }
        IdentType type = this.typeMap.get(dimension);
        this.defNewVar(ident, type, false);
        if (this.printGrammarInf) {
            System.out.println("<FuncFParam>");
        }
        return funcFParam;
    }

    private NodeBlock parseBlock(boolean isLoop) {    //Block → '{' { BlockItem } '}'
        ArrayList<NodeBlockItem> blockItems = new ArrayList<>();
        if (this.printLexicalInf) { //'{'
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        while (!this.token.isRbe()) {
            NodeBlockItem blockItem = this.parseBlockItem(isLoop);
            blockItems.add(blockItem);
        }
        if (this.printLexicalInf) { //'}'
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        NodeBlock block = new NodeBlock(this.lineNum, blockItems);
        if (printGrammarInf) {
            System.out.println("<Block>");
        }
        return block;
    }

    private NodeBlockItem parseBlockItem(boolean isLoop) {    //BlockItem → Decl | Stmt
        NodeBlockItem blockItem;
        if (this.isDecl()) {
            NodeDecl decl = this.parseDecl();
            blockItem = new NodeBlockItem(this.lineNum, decl);
        } else {
            NodeStmt stmt = this.parseStmt(isLoop);
            blockItem = new NodeBlockItem(this.lineNum, stmt);
        }
        return blockItem;
    }

    private NodeStmt parseStmt(boolean isLoop) {
        /*Stmt → 1. LVal '=' Exp ';' // 每种类型的语句都要覆盖
                |2. [Exp] ';' //有无Exp两种情况
                |3. Block
                |4. 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
                |5. 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
                |6. 'break' ';' | 'continue' ';'
                |7. 'return' [Exp] ';' // 1.有Exp 2.无Exp
                |8. LVal '=' 'getint''('')'';'
                |9. 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
         */
        NodeStmt stmt;
        if (this.token.isLbe()) {    //3. Block
            int preIndex = this.curIndex;
            this.createTable(new Symbol(index, curIndex + 1, this.token, null, false));
            NodeBlock block = this.parseBlock(isLoop);
            stmt = new NodeStmtBl(block.getLineNum(), block);
            this.curIndex = preIndex;
        } else if (this.token.isIf()) {     //4. 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
            if (this.printLexicalInf) { //'if'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.printLexicalInf) { //'('
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeCond cond = this.parseCond();
            if (this.token.isRpt()) {
                if (this.printLexicalInf) { //')'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorJ(cond.getLineNum());
            }
            NodeStmt stmtIf = this.parseStmt(isLoop);
            if (this.token.isElse()) {  //4. 'if' '(' Cond ')' Stmt  'else' Stmt 有ELSE
                if (this.printLexicalInf) { //'else'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
                NodeStmt stmtElse = this.parseStmt(isLoop);
                stmt = new NodeStmtIf(cond.getLineNum(), cond, stmtIf, stmtElse);
            } else {    //'if' '(' Cond ')' Stmt 无ELSE
                stmt = new NodeStmtIf(cond.getLineNum(), cond, stmtIf, null);
            }
        } else if (this.token.isFor()) {    //5. 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
            if (this.printLexicalInf) { //'for'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.printLexicalInf) { //'('
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeForStmt forStmt1 = null;
            if (!this.token.isSemi()) { //不缺省第一个ForStmt
                forStmt1 = this.parseForStmt();
            }
            if (this.printLexicalInf) { //';'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeCond cond = null;
            if (!this.token.isSemi()) { //不缺省cond
                cond = this.parseCond();
            }
            if (this.printLexicalInf) { //';'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeForStmt forStmt2 = null;
            if (!this.token.isRpt()) { //不缺省第二个ForStmt
                forStmt2 = this.parseForStmt();
            }
            if (this.printLexicalInf) { //')'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeStmt nodeStmt = this.parseStmt(true);
            stmt = new NodeStmtFo(this.lineNum, forStmt1, cond, forStmt2, nodeStmt);
        } else if (this.token.isBreak() || this.token.isContinue()) {   //6. 'break' ';' | 'continue' ';'
            wordTuple bc = this.token;
            if (!isLoop) {
                this.checkErrorM(bc.getWordLine());
            }
            if (this.printLexicalInf) { //'break || continue'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.token.isSemi()) {
                if (this.printLexicalInf) { //';'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorI(bc.getWordLine());
            }
            stmt = new NodeStmtBr(bc.getWordLine(), bc);
        } else if (this.token.isReturn()) { //7. 'return' [Exp] ';' // 1.有Exp 2.无Exp
            int lineNum = this.token.getWordLine();
            if (this.printLexicalInf) { //'return'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.token.isSemi()) {  //return;   无错误
                if (this.printLexicalInf) { //';'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                stmt = new NodeStmtRe(this.lineNum, null);
                this.next();
            } else if (this.isExp()) {  //return Exp;
                this.checkErrorF(lineNum);  //检查函数类型是否为void，如果是则报错
                NodeExp exp = this.parseExp(false);
                if (this.token.isSemi()) {  //return Exp; 无错误
                    if (this.printLexicalInf) {    //';'
                        System.out.println(token.getLexType() + " " + token.getWordName());
                    }
                    this.next();
                } else {    //return Exp;   分号缺失
                    lineNum = exp.getLineNum();
                    this.checkErrorI(lineNum);
                }
                stmt = new NodeStmtRe(exp.getLineNum(), exp);
            } else {    //return;   并且分号缺失
                this.checkErrorI(lineNum);
                stmt = new NodeStmtRe(lineNum, null);
            }
        } else if (this.token.isPrintf()) { //9. 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
            wordTuple funName = this.token;
            if (this.printLexicalInf) { //'printf'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.printLexicalInf) { //'('
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeFormatString formatString = this.parseFormatString();
            boolean legitimate = formatString.isLegitimate();
            int formNum = formatString.getFormNum();
            int lineNum = formatString.getLineNum();
            if (!legitimate) {
                this.checkErrorA(formatString.getLineNum());
            }
            ArrayList<NodeExp> exps = new ArrayList<>();
            while (this.token.isComa()) {
                if (this.printLexicalInf) { //','
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
                NodeExp exp = this.parseExp(false);
                exps.add(exp);
                lineNum = exp.getLineNum();
            }
            if (this.token.isRpt()) {
                if (this.printLexicalInf) { //')'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorJ(lineNum);
            }
            if (formNum != exps.size()) {
                this.checkErrorL(funName.getWordLine());
            }
            if(this.token.isSemi()) {
                if (this.printLexicalInf) { //';'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {    //分号缺失
                this.checkErrorI(lineNum);
            }
            stmt = new NodeStmtPr(funName.getWordLine(), funName, formatString, exps);
        } else {    //2. [Exp] ';' 或 1. LVal '=' Exp ';' 或  8. LVal '=' 'getint''('')'';'
            if (this.token.isSemi()) {  //';'
                if (this.printLexicalInf) { //';'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                stmt = new NodeStmtEx(this.lineNum, null);
                this.next();
            } else {    //Exp ';'   LVal '=' Exp ';'    LVal '=' 'getint''('')'';'
                int oldIndex = this.index;
                if (this.printLG) {
                    this.printLexicalInf = false;
                    this.printGrammarInf = false;
                }
                if (this.printE) {
                    this.printErrorInf = false;
                }
                NodeExp exp = this.parseExp(false);
                if (this.printLG) {
                    this.printLexicalInf = true;
                    this.printGrammarInf = true;
                }
                if (this.printE) {
                    this.printErrorInf = true;
                }
                if (!this.token.isAssign()) {  //Exp ';'
                    this.index = oldIndex;
                    this.token = this.wordList.get(index);
                    this.lineNum = token.getWordLine();
                    exp = this.parseExp(false);
                    if (this.token.isSemi()) {
                        if (this.printLexicalInf) { //';'
                            System.out.println(token.getLexType() + " " + token.getWordName());
                        }
                        this.next();
                    } else {    //分号缺失
                        this.checkErrorI(exp.getLineNum());
                    }
                    stmt = new NodeStmtEx(exp.getLineNum(), exp);
                } else {    //LVal '=' Exp ';'    LVal '=' 'getint''('')'';'
                    this.index = oldIndex;
                    this.token = this.wordList.get(index);
                    this.lineNum = token.getWordLine();
                    NodeLVal lVal = this.parseLVal();
                    int lineNum = lVal.getLineNum();
                    this.checkErrorH(lVal);
                    if (this.printLexicalInf) { //'='
                        System.out.println(token.getLexType() + " " + token.getWordName());
                    }
                    this.next();
                    if (this.token.isGet()) {   //LVal '=' 'getint''('')'';'
                        wordTuple funName = this.token;
                        if (this.printLexicalInf) { //'getint'
                            System.out.println(token.getLexType() + " " + token.getWordName());
                        }
                        this.next();
                        if (this.printLexicalInf) { //'('
                            System.out.println(token.getLexType() + " " + token.getWordName());
                        }
                        this.next();
                        if (this.token.isRpt()) {
                            if (this.printLexicalInf) { //')'
                                System.out.println(token.getLexType() + " " + token.getWordName());
                            }
                            this.next();
                        } else {
                            this.checkErrorJ(lVal.getLineNum());
                        }
                        if (this.token.isSemi()) {
                            if (this.printLexicalInf) { //';'
                                System.out.println(token.getLexType() + " " + token.getWordName());
                            }
                            this.next();
                        } else {
                            this.checkErrorI(lineNum);
                        }
                        stmt = new NodeStmtGe(lVal.getLineNum(), lVal, funName);
                    } else {    //LVal '=' Exp ';'
                        exp = this.parseExp(false);
                        if (this.token.isSemi()) {
                            if (this.printLexicalInf) { //';'
                                System.out.println(token.getLexType() + " " + token.getWordName());
                            }
                            this.next();
                        } else {
                            this.checkErrorI(exp.getLineNum());
                        }
                        stmt = new NodeStmtLv(lVal.getLineNum(), lVal, exp);
                    }
                }
            }
        }
        if (this.printGrammarInf) {
            System.out.println("<Stmt>");
        }
        return stmt;
    }

    private NodeForStmt parseForStmt() {    //ForStmt → LVal '=' Exp
        NodeLVal lVal = this.parseLVal();
        if (this.printLexicalInf) { //'='
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        NodeExp exp = this.parseExp(false);
        NodeForStmt forStmt = new NodeForStmt(lVal.getLineNum(), lVal, exp);
        if (this.printGrammarInf) {
            System.out.println("<ForStmt>");
        }
        return forStmt;
    }

    private NodeExp parseExp(boolean isInRParams) {    //表达式 Exp → AddExp 注：SysY 表达式是int 型表达式
        NodeAddExp addExp = this.parseAddExp(isInRParams);
        NodeExp exp = new NodeExp(addExp.getLineNum(), addExp);
        if (isInRParams) {
            exp.setIdentType(addExp.getIdentType());
        }
        if (this.printGrammarInf) {
            System.out.println("<Exp>");
        }
        return exp;
    }

    private NodeCond parseCond() {  //条件表达式 Cond → LOrExp
        NodeLOrExp lOrExp = this.parseLOrExp();
        NodeCond cond = new NodeCond(lOrExp.getLineNum(), lOrExp);
        if (this.printGrammarInf) {
            System.out.println("<Cond>");
        }
        return cond;
    }

    private NodeLVal parseLVal() {  //左值表达式 LVal → Ident {'[' Exp ']'} //1.普通变量 2.一维数组 3.二维数组
        wordTuple ident = this.token;
        this.checkErrorC(ident, false);
        if (this.printLexicalInf) { //'ident'
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        ArrayList<NodeExp> exps = new ArrayList<>();
        while (this.token.isLbk()) {
            if (this.printLexicalInf) { //'['
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeExp exp = this.parseExp(false);
            exps.add(exp);
            int lineNum = exp.getLineNum();
            if (this.token.isRbk()) {
                if (this.printLexicalInf) { //']'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorK(lineNum);
            }
        }
        NodeLVal lVal = new NodeLVal(ident.getWordLine(), ident, exps);
        Symbol symbol = this.findVar(ident.getWordName());
        if (symbol != null) {
            if (symbol.getType() == IdentType.COMMONVAR) {
                lVal.setIdentType(IdentType.COMMONVAR);
            } else if (symbol.getType() == IdentType.ONEDIMARR) {
                if (exps.isEmpty()) {
                    lVal.setIdentType(IdentType.ONEDIMARR);
                } else {
                    lVal.setIdentType(IdentType.COMMONVAR);
                }
            } else if (symbol.getType() == IdentType.TWODIMARR) {
                if (exps.isEmpty()) {
                    lVal.setIdentType(IdentType.TWODIMARR);
                } else if (exps.size() == 1) {
                    lVal.setIdentType(IdentType.ONEDIMARR);
                } else {
                    lVal.setIdentType(IdentType.COMMONVAR);
                }
            }
        }
        if (this.printGrammarInf) {
            System.out.println("<LVal>");
        }
        return lVal;
    }

    private NodePrimaryExp parsePrimaryExp(boolean isInRParams) {  //基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number // 三种情况均需覆盖
        NodePrimaryExp primaryExp;
        if (this.token.isLpt()) {
            if (this.printLexicalInf) { //'('
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeExp exp = this.parseExp(isInRParams);
            if (this.printLexicalInf) { //')'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            primaryExp = new NodePrimaryExp(exp.getLineNum(), exp);
            if (isInRParams) {
                primaryExp.setIdentType(exp.getIdentType());
            }
        } else if (this.token.isNumber()) {
            NodeNumber number = this.parseNumber();
            primaryExp = new NodePrimaryExp(number.getLineNum(), number);
            if (isInRParams) {
                primaryExp.setIdentType(number.getIdentType());
            }
        } else {
            NodeLVal lVal = this.parseLVal();
            primaryExp = new NodePrimaryExp(lVal.getLineNum(), lVal);
            if (isInRParams) {
                primaryExp.setIdentType(lVal.getIdentType());
            }
        }
        if (this.printGrammarInf) {
            System.out.println("<PrimaryExp>");
        }
        return primaryExp;
    }

    private NodeNumber parseNumber() {  //数值 Number → IntConst
        wordTuple number = this.token;
        if (this.printLexicalInf) { //'数字'
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        NodeNumber nodeNumber = new NodeNumber(number.getWordLine(), number);
        nodeNumber.setIdentType(IdentType.COMMONVAR);
        if (this.printGrammarInf) {
            System.out.println("<Number>");
        }
        return nodeNumber;
    }

    private NodeUnaryExp parseUnaryExp(boolean isInRParams) {  //一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        NodeUnaryExp unaryExp;
        if (this.token.isIdent() && this.wordList.get(index + 1).isLpt()) { //Ident '(' [FuncRParams] ')'
            wordTuple ident = this.token;
            int lineNum = ident.getWordLine();
            this.checkErrorC(ident, true);
            if (this.printLexicalInf) { //'ident'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            if (this.printLexicalInf) { //'('
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            NodeFuncRParams funcRParams = null;
            int rParamSize = 0;
            if (this.isFuncRParams()) {
                funcRParams = this.parseFuncRParams();
                lineNum = funcRParams.getLineNum();
                rParamSize = funcRParams.getRParamsSize();
                if (this.checkErrorD(ident, rParamSize)) {  //无数量匹配的错误
                    this.checkErrorE(ident, funcRParams.getTypeList());
                }
            }
            if (this.token.isRpt()) {
                if (funcRParams == null) {
                    this.checkErrorD(ident, 0);
                }
                if (this.printLexicalInf) { //')'
                    System.out.println(token.getLexType() + " " + token.getWordName());
                }
                this.next();
            } else {
                this.checkErrorJ(lineNum);
            }
            unaryExp = new NodeUnaryExp(ident.getWordLine(), ident, funcRParams);
            if (isInRParams) {
                Symbol symbol = this.findFunc(ident.getWordName());
                if (symbol != null) {
                    if (symbol.isVoid()) {
                        unaryExp.setIdentType(IdentType.FUNCTION);
                    } else {
                        unaryExp.setIdentType(IdentType.COMMONVAR);
                    }
                }
            }
        } else if (this.isUnaryOp()) {  //UnaryOp UnaryExp
            NodeUnaryOp unaryOp = this.parseUnaryOp();
            NodeUnaryExp nodeUnaryExp = this.parseUnaryExp(isInRParams);
            unaryExp = new NodeUnaryExp(unaryOp.getLineNum(), unaryOp, nodeUnaryExp);
            if (isInRParams) {
                unaryExp.setIdentType(nodeUnaryExp.getIdentType());
            }
        } else {    //PrimaryExp
            NodePrimaryExp primaryExp = this.parsePrimaryExp(isInRParams);
            unaryExp = new NodeUnaryExp(primaryExp.getLineNum(), primaryExp);
            if (isInRParams) {
                unaryExp.setIdentType(primaryExp.getIdentType());
            }
        }
        if (this.printGrammarInf) {
            System.out.println("<UnaryExp>");
        }
        return unaryExp;
    }



    private NodeUnaryOp parseUnaryOp() {
        wordTuple operator = this.token;
        if (this.printLexicalInf) { //'+'|'-'|'!'
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        NodeUnaryOp unaryOp = new NodeUnaryOp(operator.getWordLine(), operator);
        if (this.printGrammarInf) {
            System.out.println("<UnaryOp>");
        }
        return unaryOp;
    }

    private NodeFuncRParams parseFuncRParams() {
        ArrayList<NodeExp> exps = new ArrayList<>();
        NodeExp exp = this.parseExp(true);
        exps.add(exp);
        while (this.token.isComa()) {
            if (this.printLexicalInf) { //','
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            exp = this.parseExp(true);
            exps.add(exp);
        }
        NodeFuncRParams funcRParams = new NodeFuncRParams(exps.get(0).getLineNum(), exps);
        funcRParams.setTypeList();
        if (this.printGrammarInf) {
            System.out.println("<FuncRParams>");
        }
        return funcRParams;
    }

    private NodeMulExp parseMulExp(boolean isInRParams) {
        NodeUnaryExp unaryExp = this.parseUnaryExp(isInRParams);
        NodeMulExp mulExp = new NodeMulExp(unaryExp.getLineNum(), unaryExp);
        if (isInRParams) {
            mulExp.setIdentType(unaryExp.getIdentType());
        }
        while (this.token.isMult() || this.token.isDiv() || this.token.isMod()) {
            if (this.printGrammarInf) {
                System.out.println("<MulExp>");
            }
            wordTuple operator = this.token;
            if (this.printLexicalInf) { //'operator'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            unaryExp = this.parseUnaryExp(false);
            mulExp = new NodeMulExp(mulExp.getLineNum(), mulExp, operator, unaryExp);
            if (isInRParams) {
                mulExp.setIdentType(IdentType.COMMONVAR);
            }
        }
        if (this.printGrammarInf) {
            System.out.println("<MulExp>");
        }
        return mulExp;
    }

    private NodeAddExp parseAddExp(boolean isInRParams) {
        NodeMulExp mulExp = this.parseMulExp(isInRParams);
        NodeAddExp addExp = new NodeAddExp(mulExp.getLineNum(), mulExp);
        if (isInRParams) {
            addExp.setIdentType(mulExp.getIdentType());
        }
        while (this.token.isPlus() || this.token.isMinu()) {
            if (this.printGrammarInf) {
                System.out.println("<AddExp>");
            }
            wordTuple operator = this.token;
            if (this.printLexicalInf) { //'operator'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            mulExp = this.parseMulExp(false);
            addExp = new NodeAddExp(addExp.getLineNum(), addExp, operator, mulExp);
            if (isInRParams) {
                addExp.setIdentType(IdentType.COMMONVAR);
            }
        }
        if (this.printGrammarInf) {
            System.out.println("<AddExp>");
        }
        return addExp;
    }

    private NodeRelExp parseRelExp() {
        NodeAddExp addExp = this.parseAddExp(false);
        NodeRelExp relExp = new NodeRelExp(addExp.getLineNum(), addExp);
        while (this.token.isLss() || this.token.isLeq() || this.token.isGre() || this.token.isGeq()) {
            if (this.printGrammarInf) {
                System.out.println("<RelExp>");
            }
            wordTuple operator = this.token;
            if (this.printLexicalInf) { //'operator'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            addExp = this.parseAddExp(false);
            relExp = new NodeRelExp(relExp.getLineNum(), relExp, operator, addExp);
        }
        if (this.printGrammarInf) {
            System.out.println("<RelExp>");
        }
        return relExp;
    }

    private NodeEqExp parseEqExp() {
        NodeRelExp relExp = this.parseRelExp();
        NodeEqExp eqExp = new NodeEqExp(relExp.getLineNum(), relExp);
        while (this.token.isEql() || this.token.isNeq()) {
            if (this.printGrammarInf) {
                System.out.println("<EqExp>");
            }
            wordTuple operator = this.token;
            if (this.printLexicalInf) { //'operator'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            relExp = this.parseRelExp();
            eqExp = new NodeEqExp(eqExp.getLineNum(), eqExp, operator, relExp);
        }
        if (this.printGrammarInf) {
            System.out.println("<EqExp>");
        }
        return eqExp;
    }

    private NodeLAndExp parseLAndExp() {
        NodeEqExp eqExp = this.parseEqExp();
        NodeLAndExp lAndExp = new NodeLAndExp(eqExp.getLineNum(), eqExp);
        while (this.token.isAnd()) {
            if (this.printGrammarInf) {
                System.out.println("<LAndExp>");
            }
            wordTuple operator = this.token;
            if (this.printLexicalInf) { //'operator'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            eqExp = this.parseEqExp();
            lAndExp = new NodeLAndExp(lAndExp.getLineNum(), lAndExp, operator, eqExp);
        }
        if (this.printGrammarInf) {
            System.out.println("<LAndExp>");
        }
        return lAndExp;
    }

    private NodeLOrExp parseLOrExp() {
        NodeLAndExp lAndExp = this.parseLAndExp();
        NodeLOrExp lOrExp = new NodeLOrExp(lAndExp.getLineNum(), lAndExp);
        while (this.token.isOr()) {
            if (this.printGrammarInf) {
                System.out.println("<LOrExp>");
            }
            wordTuple operator = this.token;
            if (this.printLexicalInf) { //'operator'
                System.out.println(token.getLexType() + " " + token.getWordName());
            }
            this.next();
            lAndExp = this.parseLAndExp();
            lOrExp = new NodeLOrExp(lOrExp.getLineNum(), lOrExp, operator, lAndExp);
        }
        if (this.printGrammarInf) {
            System.out.println("<LOrExp>");
        }
        return lOrExp;
    }

    private NodeConstExp parseConstExp() {
        NodeAddExp addExp = this.parseAddExp(false);
        if (this.printGrammarInf) {
            System.out.println("<ConstExp>");
        }
        return new NodeConstExp(addExp.getLineNum(), addExp);
    }

    private NodeFormatString parseFormatString() {
        wordTuple formatString = this.token;
        if (this.printLexicalInf) { //'formatString'
            System.out.println(token.getLexType() + " " + token.getWordName());
        }
        this.next();
        return new NodeFormatString(formatString.getWordLine(), formatString);
    }

    private boolean isUnaryOp() {
        return this.token.isPlus() || this.token.isMinu() || this.token.isNot();
    }

    private boolean isDecl() {
        return this.isConstDecl() || this.isVarDecl();
    }

    private boolean isConstDecl() {
        return this.token.isConst() && this.wordList.get(index + 1).isInt();
    }

    private boolean isVarDecl() {
        wordTuple tuple1 = this.wordList.get(index + 1);
        wordTuple tuple2 = this.wordList.get(index + 2);
        if (this.token.isInt() && tuple1.isIdent()
                && (tuple2.isLbk() || tuple2.isAssign() || tuple2.isSemi() || tuple2.isComa())) {
            return true;
        }
        return this.token.isInt() && tuple1.isIdent() && (tuple2.isInt() || tuple2.isVoid());
    }

    private boolean isFuncDef() {
        return (this.token.isInt() || this.token.isVoid()) && this.wordList.get(index + 1).isIdent();
    }

    private boolean isExp() {
        return this.token.isLpt() || this.token.isIdent() || this.token.isNumber()
                || this.token.isPlus() || this.token.isMinu() || this.token.isNot();
    }

    private boolean isFuncFParams() {
        return this.token.isInt();
    }

    private boolean isFuncRParams() {
        return this.token.isLpt() || this.token.isIdent() || this.token.isNumber()
                || this.token.isPlus() || this.token.isMinu() || this.token.isNot();
    }
}
