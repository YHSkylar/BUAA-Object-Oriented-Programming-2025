import java.math.BigInteger;

public class ExprFactor implements Factor {
    private final Expr base;
    private final int exp;

    public ExprFactor(Expr expr, int exp) {
        this.base = expr;
        this.exp = exp;
    }

    public Poly toPoly() {
        Poly poly = base.toPoly().powPoly(this.exp);
        return poly;
    }

    public Poly derive() {
        Poly poly = new Poly();
        if (exp == 0) {
            Mono mono = new Mono(BigInteger.ZERO,0);
            poly.addMono(mono);
            return poly;
        }
        else if (exp == 1) {
            return base.derive();
        }
        else {
            Mono mono = new Mono(new BigInteger(String.valueOf(this.exp)),0);
            poly.addMono(mono);

            ExprFactor exprFactor = new ExprFactor(base, exp - 1);
            poly = poly.multPoly(exprFactor.toPoly());

            poly = poly.multPoly(base.derive());
            return poly;
        }
    }
}
