package LexicalTools;

import java.util.HashMap;

public class Lexer {
    private final String source;
    private int curPos;
    private String token;
    private LexType type;
    private Object value;
    private final HashMap<String, LexType> reserveWords = new HashMap<>();
    private final HashMap<String, LexType> singleSymbol = new HashMap<>();
    private int lineNum;
    private boolean legitimate;
    private boolean isEnd;
    private boolean isAnnotation;

    public Lexer(String s) {
        source = s;
        curPos = 0;
        token = null;
        type = null;
        value = null;
        initReserveWords();
        initSingleSymbol();
        lineNum = 0;
        this.legitimate = true;
        this.isEnd = false;
        this.isAnnotation = false;
    }

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

    private void initSingleSymbol() {
        singleSymbol.put("+", LexType.PLUS);
        singleSymbol.put("-", LexType.MINU);
        singleSymbol.put("*", LexType.MULT);
        singleSymbol.put("%", LexType.MOD);
        singleSymbol.put(";", LexType.SEMICN);
        singleSymbol.put(",", LexType.COMMA);
        singleSymbol.put("(", LexType.LPARENT);
        singleSymbol.put(")", LexType.RPARENT);
        singleSymbol.put("[", LexType.LBRACK);
        singleSymbol.put("]", LexType.RBRACK);
        singleSymbol.put("{", LexType.LBRACE);
        singleSymbol.put("}", LexType.RBRACE);
    }

    private boolean isSingleSymbol(String s) {
        return this.singleSymbol.containsKey(s);
    }

    public void next() {
        while(curPos < source.length()
                && (source.charAt(curPos) == ' ' || source.charAt(curPos) == '\n'
                || source.charAt(curPos) == '\r' || source.charAt(curPos) == '\t')) {
            if (source.charAt(curPos) == '\n') {
                lineNum++;
            }
            curPos++;
        }
        if (curPos >= source.length()) {
            this.isEnd = true;
            return;
        }
        char c = source.charAt(curPos++);
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        if (Character.isLetter(c) || c == '_') {    //标识符或保留字
            while (curPos < source.length() &&
                    (Character.isDigit(source.charAt(curPos))
                            || Character.isLetter(source.charAt(curPos))
                            || source.charAt(curPos) == '_')) {
                c = source.charAt(curPos++);
                sb.append(c);
            }
            token = new String(sb);
            if (reserved(token)) {
                type = reserveWords.get(token);
            } else {
                type = LexType.IDENFR;
            }
            value = new String(sb);
            this.legitimate = true;
            this.isAnnotation = false;
        } else if(Character.isDigit(c)) {       //无符号整数
            while(curPos < source.length() && Character.isDigit(source.charAt(curPos))) {
                c = source.charAt(curPos++);
                sb.append(c);
            }
            token = new String(sb);
            type = LexType.INTCON;
            value = Integer.valueOf(token);
            this.legitimate = true;
            this.isAnnotation = false;
        } else if (c == '"') {                  //形式化字符串
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
            this.legitimate = this.checkErrorA();
            this.isAnnotation = false;
        } else if (c == '|') {                 //"||"
            if (curPos < source.length() && source.charAt(curPos) == '|') {
                c = source.charAt(curPos++);
                sb.append(c);
                token = new String(sb);
                type = LexType.OR;
                value = new String(sb);
                this.legitimate = true;
            } else {
                token = new String(sb);
                type = null;
                value = new String(sb);
                this.legitimate = false;
            }
            this.isAnnotation = false;
        } else if (c == '&') {              //"&&"
            if (curPos < source.length() && source.charAt(curPos) == '&') {
                c = source.charAt(curPos++);
                sb.append(c);
                token = new String(sb);
                type = LexType.AND;
                value = new String(sb);
                this.legitimate = true;
            } else {
                token = new String(sb);
                type = null;
                value = new String(sb);
                this.legitimate = false;
            }
            this.isAnnotation = false;
        } else if (c == '<') {              //"<=" 和 "<"
            if (curPos < source.length() && source.charAt(curPos) == '=') {
                c = source.charAt(curPos++);
                sb.append(c);
                type = LexType.LEQ;
            } else {
                type = LexType.LSS;
            }
            token = new String(sb);
            value = new String(sb);
            this.legitimate = true;
            this.isAnnotation = false;
        } else if (c == '>') {              //">=" 和 ">"
            if (curPos < source.length() && source.charAt(curPos) == '=') {
                c = source.charAt(curPos++);
                sb.append(c);
                type = LexType.GEQ;
            } else {
                type = LexType.GRE;
            }
            token = new String(sb);
            value = new String(sb);
            this.legitimate = true;
            this.isAnnotation = false;
        } else if (c == '!') {             //"!=" 和 "!"
            if (curPos < source.length() && source.charAt(curPos) == '=') {
                c = source.charAt(curPos++);
                sb.append(c);
                type = LexType.NEQ;
            } else {
                type = LexType.NOT;
            }
            token = new String(sb);
            value = new String(sb);
            this.legitimate = true;
            this.isAnnotation = false;
        } else if (c == '=') {          //"==" 和 "="
            if (curPos < source.length() && source.charAt(curPos) == '=') {
                c = source.charAt(curPos++);
                sb.append(c);
                type = LexType.EQL;
            } else {
                type = LexType.ASSIGN;
            }
            token = new String(sb);
            value = new String(sb);
            this.legitimate = true;
            this.isAnnotation = false;
        } else if (c == '/') {              //注释和除号
            if (curPos < source.length() && source.charAt(curPos) == '/') {
                c = source.charAt(curPos++);
                sb.append(c);
                while (curPos < source.length() && source.charAt(curPos) != '\n') {
                    c = source.charAt(curPos++);
                    sb.append(c);
                }
                if (curPos < source.length()) {
                    c = source.charAt(curPos++);
                    sb.append(c);
                    lineNum++;
                }
                type = null;
                this.isAnnotation = true;
            } else if (curPos < source.length() && source.charAt(curPos) == '*') {
                c = source.charAt(curPos++);
                sb.append(c);
                while (curPos < source.length()) {
                    while (curPos < source.length() && source.charAt(curPos) != '*') {
                        c = source.charAt(curPos++);
                        sb.append(c);
                        if (c == '\n') {
                            lineNum++;
                        }
                    }
                    while (curPos < source.length() && source.charAt(curPos) == '*') {
                        c = source.charAt(curPos++);
                        sb.append(c);
                    }
                    if (curPos < source.length() && source.charAt(curPos) == '/') {
                        c = source.charAt(curPos++);
                        sb.append(c);
                        break;
                    }
                }
                type = null;
                this.isAnnotation = true;
            } else {
                type = LexType.DIV;
                this.isAnnotation = false;
            }
            token = new String(sb);
            value = new String(sb);
            this.legitimate = true;
        } else if (isSingleSymbol(String.valueOf(c))) {
            token = new String(sb);
            type = this.singleSymbol.get(token);
            value = new String(sb);
            this.legitimate = true;
            this.isAnnotation = false;
        } else {
            token = new String(sb);
            type = null;
            value = new String(sb);
            this.legitimate = false;
            this.isAnnotation = false;
        }
    }

    public String getToken() {
        return this.token;
    }

    public LexType getLexType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public int getLineNum() {
        return this.lineNum;
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public boolean isAnnotation() {
        return this.isAnnotation;
    }

    public boolean isLegitimate() {
        return this.legitimate;
    }

    private boolean checkErrorA() { //十进制编码为32,33,40-126的ASCII字符，'\'（编码92）出现当且仅当为'\n'
        int length = this.token.length();
        if (this.token.charAt(0) != '"' || this.token.charAt(length - 1) != '"') {
            return false;
        }
        for (int i = 1;i < this.token.length() - 1;i++) {
            char c = this.token.charAt(i);
            if (c == '%') {
                if (i + 1 >= this.token.length() || this.token.charAt(i + 1) != 'd') {
                    return false;
                }
            } else {
                if (c < 32 || c > 126 || (c > 33 && c < 40)) {
                    return false;
                }
                if (c == 92 && (i + 1 >= this.token.length() || this.token.charAt(i + 1) != 'n')) {
                    return false;
                }
            }
        }
        return true;
    }
}
