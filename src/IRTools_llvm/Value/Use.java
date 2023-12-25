package IRTools_llvm.Value;

public class Use {
    private final User user;
    private final Value value;
    private final int pos;

    public Use(User user, Value value, int pos) {
        this.user = user;
        this.value = value;
        this.pos = pos;
    }

    public User getUser() {
        return user;
    }

    public Value getValue() {
        return value;
    }

    public int getPos() {
        return pos;
    }
}
