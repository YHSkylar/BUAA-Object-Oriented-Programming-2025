public class FuncFactor implements Factor {
    private Expr expr;
    private final Func func;
    private final int num;
    private final String para1;
    private final String para2;

    public FuncFactor(Func func, int n, String para1, String para2) {
        this.func = func;
        this.num = n;
        this.para1 = para1;
        this.para2 = para2;
        setExpr();
    }

    public void setExpr() {
        String temp = func.getFunctions().get(num);
        int type = func.getType();
        String frontParaOld = func.getFrontParaOld();
        String behindParaOld = func.getBehindParaOld();
        if (type == 0) {
            String string = temp.replace(frontParaOld, "(" + para1 + ")");
            Lexer lexer = new Lexer(string);
            Parser parser = new Parser(lexer,func); //second bug
            expr = parser.parseExpr();
        }
        else {
            String string1 = temp.replace(frontParaOld, "a"); //first bug
            String string2 = string1.replace(behindParaOld, "b");
            String string3 = string2.replace("a", "(" + para1 + ")");
            String string4 = string3.replace("b", "(" + para2 + ")");
            Lexer lexer = new Lexer(string4);
            Parser parser = new Parser(lexer,func);
            expr = parser.parseExpr();
        }
    }

    public Poly toPoly() {
        return expr.toPoly();
    }
}
