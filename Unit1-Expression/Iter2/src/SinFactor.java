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
}
