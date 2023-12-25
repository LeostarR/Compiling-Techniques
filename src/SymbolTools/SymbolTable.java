package SymbolTools;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final int id;
    private final int fatherId;
    private final Symbol symbol;    //函数名，最外层函数的此值为null
    private final SymbolTable preTable;
    private final ArrayList<SymbolTable> nextTables = new ArrayList<>();
    private final HashMap<String, Symbol> varSets;
    private final HashMap<String, Symbol> funcSets;

    public SymbolTable(int id, int fatherId, Symbol symbol, SymbolTable preTable) {
        this.id = id;
        this.fatherId = fatherId;
        this.symbol = symbol;
        this.preTable = preTable;
        this.varSets = new HashMap<>();
        this.funcSets = new HashMap<>();
    }

    public void addVar(String s, Symbol symbol) {
        this.varSets.put(s, symbol);
    }

    public void addFunc(String s, Symbol symbol) {
        this.funcSets.put(s, symbol);
    }

    public boolean containsVar(String s) {
        return this.varSets.containsKey(s);
    }

    public boolean containsFunc(String s) {
        return this.funcSets.containsKey(s);
    }

    public int getId() {
        return id;
    }

    public int getFatherId() {
        return fatherId;
    }

    public SymbolTable getPreTable() {
        return preTable;
    }

    public ArrayList<SymbolTable> getNextTables() {
        return nextTables;
    }

    public void addNextTable(SymbolTable sonTable) {
        this.nextTables.add(sonTable);
    }

    public boolean isVoid() {
        if (this.symbol == null) {
            return false;
        }
        return this.symbol.isVoid();
    }

    public boolean isInt() {
        if (this.symbol == null) {
            return false;
        }
        return !this.symbol.isVoid();
    }

    public Symbol getVar(String key) {
        if (this.containsVar(key)) {
            return this.varSets.get(key);
        } else {
            return null;
        }
    }

    public Symbol getFunc(String key) {
        if (this.containsFunc(key)) {
            return this.funcSets.get(key);
        } else {
            return null;
        }
    }
}

