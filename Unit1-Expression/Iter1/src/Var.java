import java.math.BigInteger;

public class Var implements Factor {
    private final String var;
    private final int exp;

    public Var(String var, int exp) {
        this.var = var;
        this.exp = exp;
    }

    public Poly toPoly() {
        BigInteger biginteger = new BigInteger("1");
        Mono mono = new Mono(biginteger,this.exp);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }

}
