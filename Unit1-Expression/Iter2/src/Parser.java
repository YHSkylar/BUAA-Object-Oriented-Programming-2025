import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;
    private Func func;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Parser(Lexer lexer, Func func) {
        this.lexer = lexer;
        this.func = func;
    }

    public Expr parseExpr() {
        int sign = 1;
        if (lexer.peek().equals("-")) {
            sign = -1;
            lexer.next();
        }
        else if (lexer.peek().equals("+")) {
            lexer.next();
        }

        Expr expr = new Expr();
        expr.addTerm(parseTerm(sign));

        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            if (lexer.peek().equals("-")) {
                sign = -1;
            }
            else {
                sign = 1;
            }
            lexer.next();
            expr.addTerm(parseTerm(sign));

        }

        return expr;
    }

    public Term parseTerm(int sign) {
        Term term = new Term(sign);
        term.addFactor(parseFactor());

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
        }

        return term;
    }

    public Factor parseFactor() {
        if (lexer.peek().equals("(")) {
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            if (lexer.peek().equals("^")) {
                lexer.next();
                ExprFactor exprFactor = new ExprFactor(expr,Integer.parseInt(lexer.peek()));
                lexer.next();
                return exprFactor;
            }
            else {
                return new ExprFactor(expr,1);
            }
        }
        else if (Character.isLetter(lexer.peek().charAt(0))) {
            char temp = lexer.peek().charAt(0);
            if (temp == 'x') {
                lexer.next();
                if (lexer.peek().equals("^")) {
                    lexer.next();
                    Var var = new Var("x",Integer.parseInt(lexer.peek()));
                    lexer.next();
                    return var;
                }
                else {
                    return new Var("x",1);
                }
            }
            else if (temp == 's') { // sin
                return parseSinFactor();
            }
            else if (temp == 'c') { // cos
                return parseCosFactor();
            }
            else if (temp == 'f') {
                return parseFuncFactor();
            }
            else {
                return null;
            }
        }
        else if (Character.isDigit(lexer.peek().charAt(0))) {
            BigInteger num = new BigInteger(lexer.peek());
            lexer.next();
            return new Num(num);
        }
        else if (lexer.peek().equals("-")) {
            lexer.next();
            BigInteger num = new BigInteger(lexer.peek());
            num = num.negate();
            lexer.next();
            return new Num(num);
        }
        else {
            return null;
        }
    }

    public SinFactor parseSinFactor() {
        lexer.next();
        lexer.next();
        lexer.next();
        lexer.next();
        Expr expr = parseExpr();
        lexer.next();
        if (lexer.peek().equals("^")) {
            lexer.next();
            SinFactor sinFactor = new SinFactor(expr,Integer.parseInt(lexer.peek()));
            lexer.next();
            return sinFactor;
        }
        else {
            return new SinFactor(expr,1);
        }
    }

    public CosFactor parseCosFactor() {
        lexer.next();
        lexer.next();
        lexer.next();
        lexer.next();
        Expr expr = parseExpr();
        lexer.next();
        if (lexer.peek().equals("^")) {
            lexer.next();
            CosFactor cosFactor = new CosFactor(expr,Integer.parseInt(lexer.peek()));
            lexer.next();
            return cosFactor;
        }
        else {
            return new CosFactor(expr,1);
        }
    }

    public FuncFactor parseFuncFactor() {
        lexer.next();
        lexer.next();
        final int n = Integer.parseInt(lexer.peek());
        lexer.next();
        lexer.next();
        lexer.next();
        int type = func.getType();
        if (type == 0) {
            String para1 = getString();
            String para2 = null;
            lexer.next();
            return new FuncFactor(func,n,para1,para2);
        }
        else {
            String para1 = getString();
            lexer.next();
            String para2 = getString();
            lexer.next();
            return new FuncFactor(func,n,para1,para2);
        }
    }

    public String getString() {
        int flag = 1;
        StringBuilder sb = new StringBuilder();
        for (; ;lexer.next()) {
            if (lexer.peek().equals("(")) {
                flag += 1;
                sb.append(lexer.peek());
            }
            else if (lexer.peek().equals(")")) {
                flag -= 1;
                if (flag == 0) {
                    break;
                }
                else {
                    sb.append(lexer.peek());
                }
            }
            else if (lexer.peek().equals(",") && flag == 1) {
                break;
            }
            else {
                sb.append(lexer.peek());
            }
        }
        return sb.toString();
    }
}
