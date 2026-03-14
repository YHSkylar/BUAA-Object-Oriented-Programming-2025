import java.math.BigInteger;

public class SinFactor implements Factor {
    private final Expr expr;
    private final int exp;

    public SinFactor(Expr expr, int exp) {
        this.expr = expr;
        this.exp = exp;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        Mono mono = new Mono(this.expr.toPoly(),this.exp,"sin");
        poly.addMono(mono);
        return poly;
    }

    public Poly derive() {
        Poly poly = new Poly();
        if (exp == 0) {
            Mono mono = new Mono(BigInteger.ZERO,0);
            poly.addMono(mono);
        }
        else if (exp == 1) {
            Mono mono = new Mono(this.expr.toPoly(),1,"cos");
            poly.addMono(mono);
            poly = poly.multPoly(this.expr.derive());
        }
        else {
            Mono mono = new Mono(new BigInteger(String.valueOf(this.exp)),0);
            poly.addMono(mono);

            Mono mono1 = new Mono(this.expr.toPoly(),this.exp - 1,"sin");
            Poly poly1 = new Poly();
            poly1.addMono(mono1);
            poly = poly.multPoly(poly1);

            Mono mono2 = new Mono(this.expr.toPoly(),1, "cos");
            Poly poly2 = new Poly();
            poly2.addMono(mono2);
            poly = poly.multPoly(poly2);

            poly = poly.multPoly(this.expr.derive());
        }
        return poly;
    }
}
