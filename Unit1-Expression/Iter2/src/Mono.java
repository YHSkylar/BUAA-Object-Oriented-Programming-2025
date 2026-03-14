import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Mono {
    private BigInteger coe;
    private int exp;
    private HashMap<Poly,Integer> sinList = new HashMap<>();
    private HashMap<Poly,Integer> cosList = new HashMap<>();

    public Mono(BigInteger coe, int exp) {
        this.coe = coe;
        this.exp = exp;
    }

    public Mono(Poly poly, int exp, String string) {
        this.coe = BigInteger.ONE;
        this.exp = 0;
        if (string.equals("sin")) {
            sinList.put(poly,exp);
        }
        else {
            cosList.put(poly,exp);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (coe.equals(BigInteger.ZERO)) {
            return "0";
        }

        if (exp == 0) {
            sb.append(coe);
        }
        else if (exp == 1) {
            if (!coe.equals(BigInteger.ONE)) {
                sb.append(coe);
                sb.append("*");
            }
            sb.append("x");
        }
        else {
            if (!coe.equals(BigInteger.ONE)) {
                sb.append(coe);
                sb.append("*");
            }
            sb.append("x^");
            sb.append(exp);
        }

        if (!sinList.isEmpty()) {
            for (Map.Entry<Poly,Integer> entry : sinList.entrySet()) {
                sb.append("*sin((");
                sb.append(entry.getKey());
                sb.append("))");
                if (entry.getValue() != 1) {
                    sb.append("^");
                    sb.append(entry.getValue());
                }
            }
        }

        if (!cosList.isEmpty()) {
            for (Map.Entry<Poly,Integer> entry : cosList.entrySet()) {
                sb.append("*cos((");
                sb.append(entry.getKey());
                sb.append("))");
                if (entry.getValue() != 1) {
                    sb.append("^");
                    sb.append(entry.getValue());
                }
            }
        }

        if (coe.equals(BigInteger.ONE) && exp == 0 && (!sinList.isEmpty() || !cosList.isEmpty())) {
            return sb.substring(2);
        }
        else if (coe.equals(new BigInteger("-1")) &&
                (exp != 0 || !sinList.isEmpty() || !cosList.isEmpty())) {
            return "-" + sb.substring(3);
        }
        else {
            return sb.toString();
        }
    }

    public BigInteger getCoe() {
        return coe;
    }

    public int getExp() {
        return exp;
    }

    public void setCoe(BigInteger coe) {
        this.coe = coe;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public boolean TrigListsIsEmpty() {
        return sinList.isEmpty() & cosList.isEmpty();
    }

    public HashMap<Poly, Integer> getSinList() {
        return sinList;
    }

    public HashMap<Poly, Integer> getCosList() {
        return cosList;
    }

    public void mergeSinList(HashMap<Poly, Integer> sinList) {
        for (Map.Entry<Poly, Integer> entry : sinList.entrySet()) {
            Poly poly = entry.getKey();
            Integer value = entry.getValue();
            if (this.sinList.containsKey(poly)) {
                this.sinList.put(poly,value + this.sinList.get(poly));
            }
            else {
                this.sinList.put(poly,value);
            }
        }
    }

    public void mergeCosList(HashMap<Poly, Integer> cosList) {
        for (Map.Entry<Poly, Integer> entry : cosList.entrySet()) {
            Poly poly = entry.getKey();
            Integer value = entry.getValue();
            if (this.cosList.containsKey(poly)) {
                this.cosList.put(poly,value + this.cosList.get(poly));
            }
            else {
                this.cosList.put(poly,value);
            }
        }
    }

    public boolean canAdd(Mono mono) {
        // 检查指数是否相等
        if (this.getExp() != mono.getExp()) {
            return false;
        }

        // 检查 sinList 是否相等
        if (!this.getSinList().equals(mono.getSinList())) {
            return false;
        }

        // 检查 cosList 是否相等
        if (!this.getCosList().equals(mono.getCosList())) {
            return false;
        }

        return true; // 如果所有检查都通过，返回 true
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // 如果是同一个对象，直接返回 true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // 如果对象为空或类型不匹配，返回 false
        }

        Mono other = (Mono) obj; // 将对象强制转换为 Mono 类型

        // 检查系数是否相等
        if (!this.getCoe().equals(other.getCoe())) {
            return false;
        }

        // 检查指数是否相等
        if (this.getExp() != other.getExp()) {
            return false;
        }

        // 检查 sinList 是否相等
        if (!this.getSinList().equals(other.getSinList())) {
            return false;
        }

        // 检查 cosList 是否相等
        if (!this.getCosList().equals(other.getCosList())) {
            return false;
        }

        return true; // 如果所有检查都通过，返回 true
    }

    @Override
    public int hashCode() {
        int result = 17; // 初始值，通常选择一个质数
        result = 31 * result + this.getCoe().hashCode(); // 系数的哈希码
        result = 31 * result + Integer.hashCode(this.getExp()); // 指数的哈希码
        result = 31 * result + this.getSinList().hashCode(); // sinList 的哈希码
        result = 31 * result + this.getCosList().hashCode(); // cosList 的哈希码
        return result;
    }
}
