import java.math.BigInteger;

public class CosFactor implements Factor {
    private final Expr expr;
    private final int exp;

    public CosFactor(Expr expr, int exp) {
        this.expr = expr;
        this.exp = exp;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        Mono mono = new Mono(this.expr.toPoly(),this.exp,"cos");
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
            Mono mono = new Mono(this.expr.toPoly(),1,"sin");
            poly.addMono(mono);

            Mono mono1 = new Mono(new BigInteger("-1"),0);
            Poly poly1 = new Poly();
            poly1.addMono(mono1);
            poly = poly.multPoly(poly1);

            poly = poly.multPoly(this.expr.derive());
        }
        else {
            Mono mono = new Mono(new BigInteger(String.valueOf(this.exp)),0);
            poly.addMono(mono);

            Mono mono1 = new Mono(this.expr.toPoly(),this.exp - 1,"cos");
            Poly poly1 = new Poly();
            poly1.addMono(mono1);
            poly = poly.multPoly(poly1);

            Mono mono2 = new Mono(this.expr.toPoly(),1, "sin");
            Poly poly2 = new Poly();
            poly2.addMono(mono2);
            poly = poly.multPoly(poly2);

            Mono mono3 = new Mono(new BigInteger("-1"),0);
            Poly poly3 = new Poly();
            poly3.addMono(mono3);
            poly = poly.multPoly(poly3);

            poly = poly.multPoly(this.expr.derive());
        }
        return poly;
    }
}
