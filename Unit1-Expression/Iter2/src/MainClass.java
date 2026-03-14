import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        scanner.nextLine();
        Func func = null;
        if (number == 1) {
            String in0 = scanner.nextLine();
            String in1 = scanner.nextLine();
            String in2 = scanner.nextLine();
            func = new Func(in0,in1,in2);
            func.setMap();
        }
        String input = scanner.nextLine();

        StringProcessor stringprocessor = new StringProcessor(input);
        stringprocessor.process();
        String processed = stringprocessor.getString();

        Lexer lexer = new Lexer(processed);
        Parser parser = new Parser(lexer, func);

        Expr expr = parser.parseExpr();
        Poly poly = expr.toPoly();
        System.out.println(poly);
    }
}
