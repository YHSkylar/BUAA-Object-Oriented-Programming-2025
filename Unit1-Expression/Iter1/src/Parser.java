import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
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
}
