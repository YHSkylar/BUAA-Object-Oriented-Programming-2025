import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Func {
    private final HashMap<Integer, String> functions = new HashMap<>();
    private String func0;
    private String func1;
    private String funcn;
    private String frontParaOld;
    private String behindParaOld;
    private int type; // 0:single 1:double
    private String coe1;
    private String coe2;
    private String frontPara1;
    private String behindPara1;
    private String frontPara2;
    private String behindPara2;
    private String signal;
    private String exprFormula;

    public Func(String in0, String in1, String in2) {
        if (in0.charAt(2) == '0') {
            this.func0 = in0;
            if (in1.charAt(2) == '1') {
                this.func1 = in1;
                this.funcn = in2;
            }
            else {
                this.funcn = in1;
                this.func1 = in2;
            }
        }
        else if (in0.charAt(2) == '1') {
            this.func1 = in0;
            if (in1.charAt(2) == '0') {
                this.func0 = in1;
                this.funcn = in2;
            }
            else {
                this.funcn = in1;
                this.func0 = in2;
            }
        }
        else {
            this.funcn = in0;
            if (in1.charAt(2) == '0') {
                this.func0 = in1;
                this.func1 = in2;
            }
            else {
                this.func1 = in1;
                this.func0 = in2;
            }
        }
        processFuncString();
        getProperties();
    }

    public void processFuncString() {
        StringProcessor stringprocessor1 = new StringProcessor(funcn);
        stringprocessor1.process();
        this.funcn = stringprocessor1.getString();

        StringProcessor stringprocessor2 = new StringProcessor(func0);
        stringprocessor2.process();
        this.func0 = stringprocessor2.getString();

        StringProcessor stringprocessor3 = new StringProcessor(func1);
        stringprocessor3.process();
        this.func1 = stringprocessor3.getString();
    }

    public void getProperties() {
        Lexer lexer = new Lexer(funcn);
        for (int i = 0; i < 5; i++) {
            lexer.next();
        }
        this.frontParaOld = lexer.peek();
        lexer.next();
        if (lexer.peek().equals(",")) {
            this.type = 1;
            lexer.next();
            this.behindParaOld = lexer.peek();
            lexer.next();
        }
        else {
            this.type = 0;
            this.behindParaOld = null;
        }
        lexer.next();
        lexer.next(); //=
        coe1 = getCoe(lexer);
        for (int i = 0; i < 9; i++) {
            lexer.next(); //*f{n-1}(
        }
        if (type == 0) {
            frontPara1 = getString(lexer);
            behindPara1 = null;
        }
        else {
            frontPara1 = getString(lexer);
            lexer.next();
            behindPara1 = getString(lexer);
        }
        lexer.next();
        signal = lexer.peek();
        lexer.next();
        coe2 = getCoe(lexer);
        for (int i = 0; i < 9; i++) {
            lexer.next(); //*f{n-1}(
        }
        if (type == 0) {
            frontPara2 = getString(lexer);
            behindPara2 = null;
        }
        else {
            frontPara2 = getString(lexer);
            lexer.next();
            behindPara2 = getString(lexer);
        }
        lexer.next();
        if (lexer.peek().equals(")")) {
            exprFormula = null;
        }
        else {
            exprFormula = getExprFormula(lexer);
        }
    }

    public String getCoe(Lexer lexer) {
        if (lexer.peek().equals("-")) {
            lexer.next();
            return "-" + lexer.peek();
        }
        else if (Character.isDigit(lexer.peek().charAt(0))) {
            return lexer.peek();
        }
        else if (lexer.peek().equals("+")) {
            lexer.next();
            return lexer.peek();
        }
        else {
            return null;
        }
    }

    public String getString(Lexer lexer) {
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
            else if (lexer.peek().equals(",")) {
                break;
            }
            else {
                sb.append(lexer.peek());
            }
        }
        return sb.toString();
    }

    public String getExprFormula(Lexer lexer) {
        StringBuilder sb = new StringBuilder();
        for (; ! lexer.isEnd(); lexer.next()) {
            sb.append(lexer.peek());
        }
        sb.append(lexer.peek());
        return sb.toString();
    }

    public void setMap() {
        setFunc0and1();
        setFuncn(2);
        setFuncn(3);
        setFuncn(4);
        setFuncn(5);
    }

    public void setFunc0and1() {
        String fn0 = getAfterEqualSign(func0);
        functions.put(0,fn0);
        String fn1 = getAfterEqualSign(func1);
        functions.put(1, fn1);
    }

    public String getAfterEqualSign(String input) {
        String regex = "=(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else {
            return null;
        }
    }

    public void setFuncn(int n) {
        if (type == 0) {
            String fnSub1 = functions.get(n - 1);
            String fnSub2 = functions.get(n - 2);
            String tempSub1 = fnSub1.replace(frontParaOld, "(" + frontPara1 + ")");
            String tempSub2 = fnSub2.replace(frontParaOld, "(" + frontPara2 + ")");
            String fn = coe1 + "*(" + tempSub1 + ")" + signal + coe2 + "*(" + tempSub2 + ")";
            if (exprFormula != null) {
                fn = fn + exprFormula;
            }
            functions.put(n,fn);
        }
        else {
            String fnSub1 = functions.get(n - 1);
            String tempSub1 = fnSub1.replace(frontParaOld, "a");  // first bug
            tempSub1 = tempSub1.replace(behindParaOld, "b");
            tempSub1 = tempSub1.replace("a", "(" + frontPara1 + ")");
            tempSub1 = tempSub1.replace("b", "(" + behindPara1 + ")");
            String fnSub2 = functions.get(n - 2);
            String tempSub2 = fnSub2.replace(frontParaOld, "a");
            tempSub2 = tempSub2.replace(behindParaOld, "b");
            tempSub2 = tempSub2.replace("a", "(" + frontPara2 + ")");
            tempSub2 = tempSub2.replace("b", "(" + behindPara2 + ")");
            String fn = coe1 + "*(" + tempSub1 + ")" + signal + coe2 + "*(" + tempSub2 + ")";
            if (exprFormula != null) {
                fn = fn + exprFormula;
            }
            functions.put(n,fn);
        }
    }

    public HashMap<Integer, String> getFunctions() {
        return functions;
    }

    public String getFuncn() {
        return funcn;
    }

    public String getFunc0() {
        return func0;
    }

    public int getType() {
        return type;
    }

    public String getFunc1() {
        return func1;
    }

    public String getFrontParaOld() {
        return frontParaOld;
    }

    public String getBehindParaOld() {
        return behindParaOld;
    }

    public String getCoe1() {
        return coe1;
    }

    public String getCoe2() {
        return coe2;
    }

    public String getFrontPara1() {
        return frontPara1;
    }

    public String getBehindPara1() {
        return behindPara1;
    }

    public String getFrontPara2() {
        return frontPara2;
    }

    public String getBehindPara2() {
        return behindPara2;
    }

    public String getSignalOut() {
        return signal;
    }

    public String getExpr() {
        return exprFormula;
    }
}
