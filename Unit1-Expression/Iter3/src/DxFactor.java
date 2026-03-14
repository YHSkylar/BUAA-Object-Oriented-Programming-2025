public class DxFactor implements Factor {
    private final Expr expr;
    private final Func func;

    public DxFactor(Expr expr, Func func) {
        this.expr = expr;
        this.func = func;
    }

    public Poly toPoly() {
        return expr.derive();
    }

    public Poly derive() {
        Poly poly = expr.derive();
        String string = poly.toString();
        Lexer lexer = new Lexer(string);
        Parser parser = new Parser(lexer,func);
        Expr expr = parser.parseExpr();
        return expr.derive();
    }
}
