## 如何使用

1. 文件`testfile.txt`是被编译的源代码，请保证文件内容符合规定的文法（文法见`./2023编译实验文法说明.pdf`），如果有错误也请保证在课程要求的错误处理（`./错误处理要求.pdf`）范围内
2. 启动`IDE`，在`./src/compiler.java`中点击运行即可
3. 如果源文件有语法错误，错误信息会输出到`error.tx`t中，无错误的情况下会生成`llvm_ir.txt`和`mips.txt`两个文件，分别包含编译的中间代码（`LLVM`）和目标代码（`MIPS`汇编语言）

## 参考编译器设计

参考：`pl0-compiler.doc`

##### 总体结构

文档介绍了一个用Pascal语言编写的`pl0`语言的编译器，它包括四个主要部分：词法分析器、语法分析器、代码生成器和解释器。

- **词法分析器**：它负责从源程序中读取字符，识别并返回单词符号，如标识符、保留字、运算符、界符等。
- **语法分析器**：它负责根据`pl0`语言的语法规则，分析单词符号的组合，构造出抽象语法树，并检查语法错误。
- **代码生成器**：它负责根据抽象语法树，生成一种中间代码，即指令序列，用于表示源程序的语义,存储在`code`数组中。
- **解释器**：负责对中间代码进行解释执行，模拟一个抽象机器，使用`栈`来存储数据和控制信息。

##### 接口设计

- 编译器的输入是一个文本文件，包含源程序的代码，文件名由用户输入。
- 编译器的输出是一个文本文件，包含中间代码的列表，以及解释执行的结果，输出到标准输出流。如果发生错误，编译器还会在标准错误流输出错误信息。

##### 文件组织

编译器的源代码包含了所有的常量、类型、变量、函数和过程的定义。文件的主要结构如下：

- 常量定义：定义了一些编译器需要用到的常量，如保留字的个数、标识符的最大长度、数字的最大位数、地址的最大值、嵌套层次的最大值、代码数组的大小等。
- 类型定义：定义了一些编译器需要用到的类型，如符号的枚举类型、标识符的字符串类型、符号集合的集合类型、指令的记录类型、符号表的记录类型等。
- 变量定义：定义了一些编译器需要用到的全局变量，如当前字符、当前符号、当前标识符、当前数字、字符计数、行长度、错误计数、代码索引、行缓冲区、指令数组、保留字数组、符号数组、助记符数组、声明开始符号集合、语句开始符号集合、因子开始符号集合、符号表等。
- 函数和过程定义：定义了一些编译器需要用到的函数和过程，如错误处理函数、获取字符过程、获取符号过程、生成指令过程、测试符号过程、分程序过程、解释执行过程、主程序过程等。



## 编译器总体设计

##### 总体结构

编译器总体结构包括词法分析器(`LexicalTools`)，语法分析器(`GrammaticalTools`)，符号表工具(`SymbolTools`)，中间代码生成器(`IRTools_llvm`)和目标代码生成器(`MIPSTools`)，以`LLVM`为中间代码生成`MIPS`目标代码

```bash
Mode          		   Length Name
----                   ------ ----
d-----       				  GrammaticalTools
d-----       				  IRTools_llvm
d-----       		  		  LexicalTools
d-----       				  MIPSTools
d-----       				  SymbolTools
-a----       			 3191 Compiler.java
```



##### 接口设计

- 每个分析器都能返回这一阶段解析的内容，在顶层模块控制输出请求
- `IRBuildFactory.java`提供了生成中间代码的接口



##### 文件组织

- 顶层模块为`Compiler.java`，读入`testfile.txt`，然后进行词法语法分析，代码生成，控制输出等操作

- 词法分析器(`LexicalTools`)，`Lexer.java`用于分析单词，生成`ArrayList<wordTuple>`:

  ```bash
  Mode                   Length Name
  ----                   ------ ----
  -a----                  11639 Lexer.java
  -a----                    363 LexType.java
  -a----                   4681 wordTuple.java
  ```

- 语法分析器(`GrammaticalTools`)，`Parser.java`遍历单词，生成语法树：

  ```bash
  Mode                   Length Name
  ----                   ------ ----
  d-----                        GrammaticalTreeDef
  -a----                  60997 Parser.java
  ```

  `GrammaticalTreeDef`是定义的树节点集合

- 符号表工具(`SymbolTools`)定义了符号(`Symbol`),符号表(`SymbolTable`)以及枚举的变量类型(`IdentType`:普通变量，1/2维数组，函数)：

  ```bash
  Mode                   Length Name
  ----                   ------ ----
  -a----                    116 IdentType.java
  -a----                   1782 Symbol.java
  -a----                   2242 SymbolTable.java
  ```

- 中间代码生成器(`IRTools_llvm`)，`Type`定义了`Value`的类型，`Value`是所有编译过程中所有`object`的抽象类，`IRBuildFactory`提供对外接口方便生成指令，`Visitor.java`进行语义分析，构建`LLVM_Module`并生成中间代码：

  ```bash
  Mode                   Length Name
  ----                   ------ ----
  d-----                        Type
  d-----                        Value
  -a----                   7070 IRBuildFactory.java
  -a----                  60290 Visitor.java
  ```

- 目标代码生成器(`MIPSTools`)，`MipsInstructions`定义指令的抽象类，`Atom`定义指令的原子部分——立即数，寄存器和标签的抽象类，`Tools`定义`MIPS`语言的结构，最后使用`LLVMParser`对上面生成的`LLVM_Module`解析，生成`MIPS_Module`并输出目标代码：

  ```bash
  Mode                   Length Name
  ----                   ------ ----
  d-----                        Atom
  d-----                        MipsInstructions
  d-----                        Tools
  -a----                  31282 LLVMParser.java
  ```

  



## 词法分析设计

##### **编码前的设计**

- 总的思路是将整个文件的内容视作一个超长字符串，将这个字符串识别出有效的信息（如标识符，关键字以及各种运算，操作符号等等）并保存，将无效信息（注释等）准确识别并丢弃

- 对需要的有效信息创建类，定义有用的字段，例如行号，类别码，名称，值（例如数字的值是int类型，而字符串的值是它本身String类型）：

  ```java
  public class wordTuple {
      private final String wordName;    	//单词名，原字符串
      private final String wordType;   	//单词类，在枚举类中
      private final Object wordValue;   	//单词值，可能是整数，也有可能是字符串
      private final int wordLine;       	//所在行数
      private int wordColumn;     		//所在列数
      private boolean legitimate; 		//是否合法，用于输出错误信息
      
      ......
  }
  ```

  列数和合法信息在本次作业中尚未使用到

- 在词法解析中，成功解析的所有单词将存入一个`ArrayList<wordTuple>`中便于之后的语法分析和本次作业的输出环节

- 在具体的解析过程中

  - 设置`curPos`表示当前的读的位置，读取过程中及时更新位置和行数
  - 每读完一个单词要掠过所有的空格换行直到下一个有效字符

- 对于单词首字符

  - 如果是字母或下划线，考虑标识符和保留字
  - 如果是数字，考虑无符号整数
  - 如果是冒号，考虑形式化字符串
  - 其他有效符号，例如'+','*','>',"=="等所有出现在指导书中的单词类别符号
  - 特别注意区分'/'和两种注释，'=='和'=','>='和'=','&&'和'&'等等

- 最后记录单词名，类型，行数等重要信息



##### **编码完成之后的修改**

- 重新定义枚举类`Lextype`表示单词类而不是简单的字符串，方便统一表示：

  ```java
  private final Lextype wordType;
  
  public String getLexType() {
          return this.wordType.toString();
  }			
  ```

  ```java
  public enum LexType {
      IDENFR, INTCON, STRCON, MAINTK, CONSTTK,
      INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK,
      NOT, AND, OR, FORTK, GETINTTK,
      PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK,
      MULT, DIV, MOD, LSS, LEQ,
      GRE, GEQ, EQL, NEQ,
      ASSIGN, SEMICN, COMMA, LPARENT, RPARENT,
      LBRACK, RBRACK, LBRACE, RBRACE
  }
  ```

  

- 设置了两个集合分别存储所有的保留字和单一符号，读取中判断是否在这两个集合中就行了，例如：

  ```java
  private void initReserveWords() {
          reserveWords.put("main", LexType.MAINTK);
          reserveWords.put("const", LexType.CONSTTK);
          reserveWords.put("int", LexType.INTTK);
          reserveWords.put("break", LexType.BREAKTK);
          reserveWords.put("continue", LexType.CONTINUETK);
          reserveWords.put("if", LexType.IFTK);
          reserveWords.put("else", LexType.ELSETK);
          reserveWords.put("for", LexType.FORTK);
          reserveWords.put("getint", LexType.GETINTTK);
          reserveWords.put("printf", LexType.PRINTFTK);
          reserveWords.put("return", LexType.RETURNTK);
          reserveWords.put("void", LexType.VOIDTK);
  }
  
  private boolean reserved(String s) {
      	return this.reserveWords.containsKey(s);
  }
  ```

- 实际读取过程中采用`StringBuilder`类型更容易逐个添加字符



## 语法分析设计

##### 编码前的设计

- 总任务是对所有单词进行语法分析并构建语法树，因此要对上一节词法分析得到的`ArrayList<wordTuple>`逐一分析
- 自定义语法树的节点类型，利用面向对象的继承性质，设置一个总的父类`MyTreeNode`，其余节点均继承此类，举例：

  ```java
  public class NodeBlock extends MyTreeNode;
  ```

  对文法每一个非终结符都做这样的操作，对于语法成分的子节点有多种情况的，可以建立多个构造方法覆盖所有的情况：

  ```java
  public class NodeAddExp extends  MyTreeNode {
      private final NodeMulExp mulExp1;
      private final NodeAddExp addExp;
      private final wordTuple operator;
      private final NodeMulExp mulExp2;
      private IdentType identType;
  
      public NodeAddExp(int lineNum, NodeMulExp mulExp1) {
          super(lineNum);
          this.mulExp1 = mulExp1;
          this.addExp = null;
          this.operator = null;
          this.mulExp2 = null;
      }
  
      public NodeAddExp(int lineNum, NodeAddExp addExp, wordTuple operator, NodeMulExp mulExp2) {
          super(lineNum);
          this.mulExp1 = null;
          this.addExp = addExp;
          this.operator = operator;
          this.mulExp2 = mulExp2;
      }
      
      ......
  }
  ```

  对于文法`AddExp → MulExp | AddExp ('+' | '−') MulExp`，使用第一个构造方法的时候对应`AddExp → MulExp`，使用第二个构造方法的时候对应`AddExp → AddExp ('+' | '−') MulExp`，判断属于哪一种语法构造只需通过`getter()`判断特定子节点是否为`null`即可。
- 在`Parser`中采用递归下降完成整个语法分析的过程，由于给定的文法不算过于复杂，大部分`parse()`不需要回溯，根据`First(非终结符)`就能确定是何种语法成分:

  ```java
  private NodeDecl parseDecl() {  //Decl → ConstDecl | VarDecl
          if (this.isConstDecl()) {
              NodeConstDecl constDecl = this.parseConstDecl();
              return new NodeDecl(constDecl.getLineNum(), constDecl);
          } else {
              NodeVarDecl varDecl = this.parseVarDecl();
              return new NodeDecl(varDecl.getLineNum(), varDecl);
          }
  }
  ```

  在解析`Stmt`的时候可能会遇到下面的情况，按照`LVal`和Exp解析都能成功解析出第一部分,这个时候需要回溯

  ```python
  //Exp ';'   LVal '=' Exp ';'    LVal '=' 'getint''('')'';'
  ```

  按照`Exp`解析，若得到下一个符号是'='则排除前一种情况，回溯，按照`LVal`解析。
- 所有语法成分解析完成后将会得到一颗完整的语法树，为之后的语义分析和代码生成做准备



##### 编码完成之后的修改

- 和设计时差别不大，但是需要新建几个继承`NodeStmt`的子节点，例如：

  ```java
  public class NodeStmtBr extends NodeStmt {
      private final wordTuple type;
  
      public NodeStmtBr(int lineNum, wordTuple type) {
          super(lineNum);
          this.type = type;
      }
  
      public wordTuple getType() {
          return type;
      }
  }
  ```

  表示`Stmt → 'break' ';' | 'continue' ';'`的情况，其他由`Stmt`推导的语法成分同理



## 错误处理设计

##### 编码前的设计

- a类错误在词法分析就可判断，当判断出字符串时，检查是否包含非法字符即可：

  ```java
  ......
  else if (c == '"') {                  //形式化字符串
              while (curPos < source.length() && source.charAt(curPos) != '"') {
                  c = source.charAt(curPos++);
                  sb.append(c);
              }
              if (curPos < source.length() && source.charAt(curPos) == '"') {
                  c = source.charAt(curPos++);
                  sb.append(c);
              }
              token = new String(sb);
              type = LexType.STRCON;
              value = new String(sb);
      /************************************************/
              this.legitimate = this.checkErrorA();	//此处判断
      /************************************************/
  }
  ......
  ```

- 需要新建符号表处理`b-h`类错误，符号表的主要结构如下：

  ```java
  public class SymbolTable {
      private final int id;
      private final int fatherId;
      private final Symbol symbol;    //函数名，最外层函数的此值为null
      private final SymbolTable preTable;
      private final ArrayList<SymbolTable> nextTables = new ArrayList<>();
      private final HashMap<String, Symbol> varSets;
      private final HashMap<String, Symbol> funcSets;
  }
  ```

  所有符号表存储在一个`ArrayList`中，`id`是符号表的索引，全局变量常量以及函数所在的符号表的`id`为0，`fatherId`是外层符号表的索引，`varSets`和`funcSets`存储当前作用域定义的变量和函数

- 在语法分析过程中定义和使用变量和函数(`ident`)之前查阅符号表即可判断`b,c`类错误：

  ```java
  private void defNewVar(wordTuple ident, IdentType identType, boolean isConst) {
          SymbolTable curTable = this.tables.get(this.curIndex);
          if (curTable.containsVar(ident.getWordName()) || curTable.containsFunc(ident.getWordName())) {    
              		//重复定义，报错
              this.checkErrorB(ident.getWordLine());
          } else {    //否则，填表
              Symbol symbol = new Symbol(this.wordList.indexOf(ident), this.curIndex, ident, identType, isConst);
              curTable.addVar(ident.getWordName(), symbol);
          }
      }
  ```

- `d,e,f,g`类错误一边解析一边完成即可，对于函数符号，根据参数信息可以判断类型正确性和参数个数正确性，根据返回类型判断`return`的正确性

- 符号缺失的错误都可以像这样判断：

  ```java
  if(this.token.isSemi()) {
                  System.out.println(token.getLexType() + " " + token.getWordName());
                  this.next();
   } else {    //分号缺失
                  this.checkErrorI(lineNum);
  }
  ```

- 在进入循环体时，传入参数表示当前处于循环中，即可判断`m`类错误



##### 编码完成之后的修改

- 在考虑重定义时既要考虑变量重定义也要考虑函数重定义，例如下面的例子是非法的：

  ```C
  int fff = 5;
  void fff() {
  	return;
  }
  ```

  所以必须检查两个`HashMap`：

  ```java
  if (curTable.containsVar(ident.getWordName()) || curTable.containsFunc(ident.getWordName())) {   
      		//重复定义，报错
              this.checkErrorB(ident.getWordLine());
  }
  ```

  之前只对同类型进行检查，忽略了其他函数和变量不同类型的重定义



## 代码生成设计

##### 编码前的设计

###### 中间代码生成`IRTools_llvm`

- `Type`:`Value`的各种类型，拿数组类型举例：

  ```java
  public class TypeArray extends Type {
      private final int numOfElement;
      private final Type typeOfElement;
  
      public TypeArray(int numOfElement, Type typeOfElement) {    
          //numOfElement->数组一维空间的size，即多少个元素, typeOfElement->每一个元素的type（应相同）
          this.numOfElement = numOfElement;
          this.typeOfElement = typeOfElement;
      }
  
      public int getNumOfElement() {
          return this.numOfElement;
      }
  
      public Type getTypeOfElement() {
          return this.typeOfElement;
      }
  
      @Override
      public int getSpace() {
          return this.numOfElement * this.typeOfElement.getSpace();
      }
  
      @Override
      public String toString() {
          return "[" + this.numOfElement + " x " + this.typeOfElement.toString() + "]";
      }
  }
  ```

  它可以指示这个数组Value的维数（如果是二维，那么`typeOfElement`也是'`TypeArray`'类型的，除此之外还重写`toString()`方法，方便输出指令

- `Value`:万物皆Value，不论是顶层的`LLVM_Module`，还是基本块`BasicBlock`，还是函数`Function`都是`Value`，因此让他们全部继承抽象类`Value.java`，而`User`一般指指令，但同时指令也是一种`Value`，因此它也要继承`Value`，`Value`和`User`类似于一条边的两个节点，关系通过`Use`维护：

  ```java
  public class User extends Value {
      protected ArrayList<Value> operands = new ArrayList<>();
      public User(String name, Type type, Value parent) {
          super(name, type, parent);
      }
  
      public void addValue(Value value) {
          this.operands.add(value);
          Use use = new Use(this, value, operands.indexOf(value));
          value.addUse(use);
      }
      ......
  }
  ```

  以`BasicBlock`为例：

  ```java
  public class BasicBlock extends Value implements Comparable<BasicBlock> {
      private final ArrayList<Instruction> instructions = new ArrayList<>();
      public BasicBlock(String name, Value parent) {
          super(name, new TypeLabel(), parent);
      }
  
      public void addInstruction(Instruction instruction) {
          this.instructions.add(instruction);
      }
  
      public ArrayList<Instruction> getInstructions() {
          return this.instructions;
      }
      ......
  }
  ```

  以二进制运算And指令为例：

  ```java
  public class BinAnd extends InstructonOfBinary {
      public BinAnd(String name, Value v1, Value v2, BasicBlock parent) {
          super(name, new TypeInt(32), parent);
          this.addValue(v1);
          this.addValue(v2);
      }
  
      @Override
      public String toString() {
          return this.getName() +
                  " = and " +
                  this.getType() +
                  " " +
                  this.getValue(0).getName() +
                  ", "  +
                  this.getValue(1).getName();
      }
  }
  ```

- `Visitor`接收语法分析得到的语法树根节点`(NodeCompUnit) compUnit`，向下递归进行语义分析，举例：

  ```java
  private void visitNodeDecl(NodeDecl decl) {                         //声明 Decl → ConstDecl | VarDecl
          NodeConstDecl constDecl = decl.getConstDecl();
          NodeVarDecl varDecl = decl.getVarDecl();
          if (varDecl == null) {
              this.visitConstDecl(constDecl);
          } else {
              this.visitNodeVarDecl(varDecl);
          }
  }
  ```

  分析过程中，借用全局变量curVal和curInt等值传递属性，生成LLVM指令：

  ```java
  private void visitNodeConstDef(NodeConstDef constDef) {             
      	//常数定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
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
              }
              ......
          }
  }
  ```

- 短路求值，对形如`if (A || B) {...} else {...}` 的语句当A为真时不能执行B而是直接跳转到A的语句块，因此 设置两个全局块`curIfBlock`和`cueElseBlock`，在递归向下解析时传递参数`flag`指示是否需要更改`cueElseBlock`的值，如果当前在解析A则需要更改`curElseBlock`为新建的基本块（描述B），如果当前在解析B则无需修改`cueElseBlock`:

  ```java
  this.visitNodeLOrExp(nodelOrExp, false);
  if (flag) {
  	this.visitNodeLAndExp(lAndExp2, true);
  } else {
  	BasicBlock nextBlock = IRBuildFactory.buildBasicBlock(this.curFunction);
  	this.curElseBlock = nextBlock;
  	this.visitNodeLAndExp(lAndExp2, true);
  	this.curBasicBlock = nextBlock;
  }
  ```

- `isCruit`指示是否为表达式，如果是，则需要通过`Icmp`指令将表达式转化为值：

  ```java
  if (this.isCruit) {
      ......
  	this.curVal = 
          IRBuildFactory.buildBinIcmp(BinIcmpType.NE, this.curVal, new ConstInt(32, 0), this.curBasicBlock);
  	this.isCruit = false;
  }
  ```

  

###### 目标代码生成`MIPSTools`

- `Atom`：指令的原子组成部分，包括寄存器，立即数还有跳转指令用到的标签，以寄存器为例：

  ```java
  public class Reg {
      private final int id;
      public Reg(int id) {
          this.id = id;
      }
  
      public String getName() {
          if (this.id == 0) {
              return "$zero";
          } else if (this.id == 1) {
              return "$at";
          } else if (this.id >= 2 && this.id <= 3) {
              return "$v" + (this.id - 2);
          } else if (this.id >= 4 && this.id <= 7) {
              return "$a" + (this.id - 4);
          } else if (this.id >= 8 && this.id <= 15) {
              return "$t" + (this.id - 8);
          } else if (this.id >=16 && this.id <= 23) {
              return "$s" + (this.id - 16);
          } else if (this.id >= 24 && this.id <= 25) {
              return "$t" + (this.id - 24 + 8);
          } else if (this.id == 28) {
              return "$gp";
          } else if (this.id == 29) {
              return "$sp";
          } else if (this.id == 30) {
              return "$fp";
          } else if (this.id == 31) {
              return "$ra";
          } else {
              return this.toString();
          }
      }
  
      @Override
      public String toString() {
          return "$" + this.id;
      }
  }
  ```

  只提供了返回寄存器名字的方法

- `MipsInstructions`：各种`MIPS`指令的抽象类，包括二进制运算指令，跳转指令，存取指令以及其他简化指令等:

  ```java
  public class BinaryPure extends MIPSInstruction {
      private final BinaryPureType opType;
      private final Reg rd;
      private final Reg rs;
      private final Reg rt;
      public BinaryPure(BinaryPureType opType, Reg rd, Reg rs, Reg rt) {
          this.opType = opType;
          this.rd = rd;
          this.rs = rs;
          this.rt = rt;
      }
  
      @Override
      public String toString() {
          return opType.toString() + " " + rd.getName() + ", " + rs.getName() + ", " + rt.getName();
      }
  }
  ```

  ```java
  public enum BinaryPureType {
      addu,
      subu,
      and,
      or;
  
      @Override
      public String toString() {
          return super.toString();
      }
  }
  ```

- `Tools`完成对MIPS代码的抽象化，包括模块`Module`，全局变量`GlobalVar`，代码块`Block`以及函数`Function`:

  ```java
  public class MIPSBlock {
      private final String blockName;
      private final BasicBlock basicBlock;
      private final MIPSFunction mipsFunction;
      private final ArrayList<MIPSInstruction> Instructions = new ArrayList<>();
      public MIPSBlock(String blockName, BasicBlock basicBlock, MIPSFunction mipsFunction) {
          this.blockName = blockName;
          this.basicBlock = basicBlock;
          this.mipsFunction = mipsFunction;
      }
  
      public void addInstruction(MIPSInstruction instruction) {
          this.Instructions.add(instruction);
      }
  
      public String getBlockName() {
          return this.blockName;
      }
  
      public BasicBlock getBasicBlock() {
          return this.basicBlock;
      }
  
      public MIPSFunction getParentMipsFunction() {
          return this.mipsFunction;
      }
  }
  ```

- 使用`LLVMParser`解析得到的`LLVMModule`，全局变量可以直接存入`Module`然后输出即可，对于函数需要分析每一条指令，根据每一条指令属于LLVM指令的类型进行生成：

  ```java
  if (instruction instanceof MemAlloca) {
  	......      
  } else if (instruction instanceof MemGetelementptr) {
  	......
  }
  	......
  ```

  

##### 编码完成之后的修改

- 增加`ZextTo`指令，如果不定义这条指令，生成的`llvm`中间代码会有问题，`i32`类型和`i1`类型不能通过`icmp`指令进行比较，因此在语义分析过程中处理`icmp`指令之前对`curVal`的`Type`进行判断




## 代码优化设计

##### 编码前的设计

###### 中端优化

- 删除无用函数，遍历`LLVM_Module`的所有函数，判断每一个函数的`userList`是否为空，（主要是`br`指令），若为空则删除并维护use-value的基本关系，遍历剩下的函数直到函数集合不发生变化
- 删除无用基本快，不可到达的基本块应该删除，并维护use-value的基本关系
- 合并基本块，从一个没有后继的基本块开始遍历，递归查找能到达这个的唯一基本块（如果不唯一应该停止查找）
- 删除死代码

###### 后端优化

- 乘除优化，乘法判端常数是否是2的整数次幂，转化为移位指令，除法将除法指令转化为乘法指令和移位指令
- 基本块合并

##### 编码完成之后的修改

删除函数的时候要对函数的每个基本快的每个指令进行use-value的维护