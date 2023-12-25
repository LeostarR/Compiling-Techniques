package LexicalTools;

public class wordTuple {
    private final String wordName;    //单词名，原字符串
    private final LexType wordType;   //单词类，在枚举类中
    private final Object wordValue;   //单词值，可能是整数，也有可能是字符串
    private final int wordLine;       //所在行数
    private int wordColumn;     //所在列数
    private final boolean legitimate; //是否合法，用于输出错误信息

    public wordTuple(String wordName, LexType wordType, Object wordValue, int wordLine, boolean legitimate) {
        this.wordName = wordName;
        this.wordType = wordType;
        this.wordValue = wordValue;
        this.wordLine = wordLine + 1;
        this.legitimate = legitimate;
    }

    public String getWordName() {
        return this.wordName;
    }

    public String getLexType() {
        return this.wordType.toString();
    }

    public int getWordLine() {
        return this.wordLine;
    }

    public boolean isLegitimate() {
        return legitimate;
    }

    public boolean isConst() {  //'const'
        return this.wordType == LexType.CONSTTK;
    }

    public boolean isInt() {    //'int'
        return this.wordType == LexType.INTTK;
    }

    public boolean isVoid() {   //'void'
        return this.wordType == LexType.VOIDTK;
    }

    public boolean isIdent() {
        return this.wordType == LexType.IDENFR;
    }

    public boolean isComa() {   //','
        return this.wordType == LexType.COMMA;
    }

    public boolean isSemi() {
        return this.wordType == LexType.SEMICN;
    }

    public boolean isLbk() {    //'['
        return this.wordType == LexType.LBRACK;
    }

    public boolean isRbk() {    //']'
        return this.wordType == LexType.RBRACK;
    }

    public boolean isLbe() {    //'{'
        return this.wordType == LexType.LBRACE;
    }

    public boolean isRbe() {    //'}'
        return this.wordType == LexType.RBRACE;
    }

    public boolean isAssign() { //'='
        return this.wordType == LexType.ASSIGN;
    }

    public boolean isLpt() {    //'('
        return this.wordType == LexType.LPARENT;
    }

    public boolean isRpt() {    //')'
        return this.wordType == LexType.RPARENT;
    }

    public boolean isMain() {   //'main'
        return this.wordType == LexType.MAINTK;
    }

    public boolean isGet() {    //'getint'
        return this.wordType == LexType.GETINTTK;
    }

    public boolean isIf() {     //'if'
        return this.wordType == LexType.IFTK;
    }

    public boolean isElse() {   //'else'
        return this.wordType == LexType.ELSETK;
    }

    public boolean isFor() {    //'for'
        return this.wordType == LexType.FORTK;
    }

    public boolean isBreak() {  //'break'
        return this.wordType == LexType.BREAKTK;
    }

    public boolean isContinue() {   //'continue'
        return this.wordType == LexType.CONTINUETK;
    }

    public boolean isReturn() { //'return'
        return this.wordType == LexType.RETURNTK;
    }

    public boolean isPrintf() { //'printf'
        return this.wordType == LexType.PRINTFTK;
    }

    public boolean isNumber() { //'NUMBER'
        return this.wordType == LexType.INTCON;
    }

    public boolean isPlus() {   //'+'
        return this.wordType == LexType.PLUS;
    }

    public boolean isMinu() {   //'-'
        return this.wordType == LexType.MINU;
    }

    public boolean isNot() {    //'!'
        return this.wordType == LexType.NOT;
    }

    public boolean isMult() {   //'*'
        return this.wordType == LexType.MULT;
    }

    public boolean isDiv() {    //'/'
        return this.wordType == LexType.DIV;
    }

    public boolean isMod() {    //'%'
        return this.wordType == LexType.MOD;
    }

    public boolean isLss() {    //'<'
        return this.wordType == LexType.LSS;
    }

    public boolean isLeq() {    //'<='
        return this.wordType == LexType.LEQ;
    }

    public boolean isGre() {    //'>'
        return this.wordType == LexType.GRE;
    }

    public boolean isGeq() {    //'>='
        return this.wordType == LexType.GEQ;
    }

    public boolean isEql() {    //'=='
        return this.wordType == LexType.EQL;
    }

    public boolean isNeq() {    //'!='
        return this.wordType == LexType.NEQ;
    }

    public boolean isAnd() {    //'&&'
        return this.wordType == LexType.AND;
    }

    public boolean isOr() {     //'||'
        return this.wordType == LexType.OR;
    }
}
