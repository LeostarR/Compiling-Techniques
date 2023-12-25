package IRTools_llvm.Value;

import IRTools_llvm.Type.Type;

import java.util.ArrayList;

public class Value {
    private final int id;
    private final String name;
    private final Type type;
    private final Value parent;
    private final ArrayList<Use> useList = new ArrayList<>();
    private final ArrayList<User> userList = new ArrayList<>();
    public static int valNum = 0;

    public Value(String name, Type type, Value parent) {
        this.id = valNum++;
        this.name = name;
        this.type = type;
        this.parent = parent;
    }

    public void addUse(Use use) {
        this.useList.add(use);
        this.userList.add(use.getUser());
    }

    public void removeUse(User user) {
        int index = this.userList.indexOf(user);
        this.useList.remove(index);
        this.userList.remove(index);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Value getParent() {
        return parent;
    }

    public ArrayList<Use> getUseList() {
        return useList;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }
}
