package SymbolTools;

import LexicalTools.wordTuple;

import java.util.ArrayList;

public class Symbol {
    private final int index;
    private final int tableId;
    private final wordTuple token;
    private final IdentType type;
    private boolean isConst;
    private int value;
    private boolean reTypeIsVoid;
    private int paramNum;
    private ArrayList<IdentType> paramTypeList;

    public Symbol(int index, int tableId, wordTuple token, IdentType type,
                  boolean isConst) {   //普通变量
        this.index = index;
        this.tableId = tableId;
        this.token = token;
        this.type = type;
        this.isConst = isConst;
    }

    public Symbol(int index, int tableId, wordTuple token, IdentType type,
                  boolean reTypeIsVoid, int paramNum) {    //函数
        this.index = index;
        this.tableId = tableId;
        this.token = token;
        this.type = type;
        this.reTypeIsVoid = reTypeIsVoid;
        this.paramNum = paramNum;
    }

    public wordTuple getToken() {
        return token;
    }

    public IdentType getType() {
        return type;
    }

    public boolean isConst() {
        return this.isConst;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setTypeList(ArrayList<IdentType> paramTypeList) {
        this.paramTypeList = paramTypeList;
    }

    public ArrayList<IdentType> getParamTypeList() {
        return this.paramTypeList;
    }

    public void setParamNum(int num) {
        this.paramNum = num;
    }

    public int getParamNum() {
        return this.paramNum;
    }

    public boolean isVoid() {
        return this.reTypeIsVoid;
    }

}
