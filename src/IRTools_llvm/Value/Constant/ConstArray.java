package IRTools_llvm.Value.Constant;

import IRTools_llvm.Type.TypeArray;

import java.util.ArrayList;

public class ConstArray extends Const {
    private final boolean isZeroInit;
    public ConstArray(ArrayList<Const> consts) {    //有初值
        super(null, new TypeArray(consts.size(), consts.get(0).getType()), null);
        for (Const con: consts) {
            this.addValue(con);
        }
        this.isZeroInit = false;
    }

    public ConstArray(TypeArray typeArray) {        //无初值，全部置0
        super(null, typeArray, null);
        this.addValue(new ZeroInitializer(typeArray));
        this.isZeroInit = true;
    }

    public boolean isZeroInit() {
        return isZeroInit;
    }

    public ArrayList<Integer> getVal() {
        ArrayList<Integer> val = new ArrayList<>();
        for (int i = 0; i < ((TypeArray) this.getType()).getNumOfElement(); i++) {
            if (this.getValue(i) instanceof ConstArray) {
                val.addAll(((ConstArray) this.getValue(i)).getVal());
            } else {
                val.add(((ConstInt) this.getValue(i)).getVal());
            }
        }
        return val;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.isZeroInit) {
            sb.append("[");
            for (int i = 0; i < ((TypeArray) this.getType()).getNumOfElement(); i++) {
                sb.append(this.getValue(i).getType()).append(" ");
                sb.append(this.getValue(i)).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("]");
        } else {
            sb.append("zeroinitializer");
        }
        return sb.toString();
    }
}
