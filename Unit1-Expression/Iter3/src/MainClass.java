import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int defFuncCount = scanner.nextInt();
        scanner.nextLine();

        String input1 = null;
        String input2 = null;
        if (defFuncCount == 1) {
            input1 = scanner.nextLine();
        }
        else if (defFuncCount == 2) {
            input1 = scanner.nextLine();
            input2 = scanner.nextLine();
        }

        int number = scanner.nextInt();
        scanner.nextLine();

        Func func;
        if (number == 1) {
            String in0 = scanner.nextLine();
            String in1 = scanner.nextLine();
            String in2 = scanner.nextLine();
            func = new Func(in0,in1,in2,input1,input2);
        }
        else {
            func = new Func(null, null, null, input1, input2);
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
