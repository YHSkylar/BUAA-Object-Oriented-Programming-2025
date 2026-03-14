import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        StringProcessor stringprocessor = new StringProcessor(input);
        stringprocessor.process();
        String processed = stringprocessor.getString();

        Lexer lexer = new Lexer(processed);
        Parser parser = new Parser(lexer);

        Expr expr = parser.parseExpr();
        Poly poly = expr.toPoly();
        System.out.println(poly);
    }
}
