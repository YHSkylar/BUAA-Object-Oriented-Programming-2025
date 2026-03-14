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
        String temp = func.getFunction(num);
        int type;
        String frontParaOld; //need change
        String behindParaOld;  //need change
        if (num <= 5 && num >= 0) {
            frontParaOld = func.getRecurseFrontParaOld();
            behindParaOld = func.getRecurseBehindParaOld();
            type = func.getRecurseFuncType();
        }
        else if (num == 7) {
            frontParaOld = func.getSelDefFrontParaOld("g");
            behindParaOld = func.getSelDefBehindParaOld("g");
            type = func.getSelDefType("g");
        }
        else {
            frontParaOld = func.getSelDefFrontParaOld("h");
            behindParaOld = func.getSelDefBehindParaOld("h");
            type = func.getSelDefType("h");
        }
        if (type == 0) {
            String string = temp.replace(frontParaOld, "(" + para1 + ")");
            Lexer lexer = new Lexer(string);
            Parser parser = new Parser(lexer,func); //second bug
            expr = parser.parseExpr();
        }
        else {
            String string1 = temp.replace(frontParaOld, "a");
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

    public Poly derive() {
        return expr.derive();
    }
}
