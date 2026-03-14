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
}
