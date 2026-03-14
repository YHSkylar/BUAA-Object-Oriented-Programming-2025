import java.math.BigInteger;

public class Mono {
    private BigInteger coe;
    private int exp;

    public Mono(BigInteger coe, int exp) {
        this.coe = coe;
        this.exp = exp;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp == 0) {
            sb.append(coe);
        }
        else if (exp == 1) {
            if (coe.equals(BigInteger.ONE)) {
                sb.append("x");
            }
            else {
                sb.append(coe);
                sb.append("*");
                sb.append("x");
            }
        }
        else {
            if (coe.equals(BigInteger.ONE)) {
                sb.append("x");
                sb.append("^");
                sb.append(exp);
            }
            else {
                sb.append(coe);
                sb.append("*");
                sb.append("x");
                sb.append("^");
                sb.append(exp);
            }
        }

        return sb.toString();
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
}
