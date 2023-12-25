package IRTools_llvm.Value;

import IRTools_llvm.Type.Type;

import java.util.ArrayList;

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

    public void removeValue(Value value) {
        this.operands.remove(value);
        value.removeUse(this);
    }

    public void replaceValue(Value oldValue, Value newValue) {
        int index = this.operands.indexOf(oldValue);
        this.operands.set(index, newValue);
        Use use = new Use(this, newValue, index);
        oldValue.removeUse(this);
        newValue.addUse(use);
    }

    public ArrayList<Value> getOperand() {
        return this.operands;
    }

    public Value getValue(int index) {
        return this.operands.get(index);
    }

    public int getNumofValues() {
        return this.operands.size();
    }
}
