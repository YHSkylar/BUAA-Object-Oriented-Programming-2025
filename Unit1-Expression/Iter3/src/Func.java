import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Func {
    private final HashMap<Integer, String> functions = new HashMap<>();
    private String func0;
    private String func1;
    private String funcn;
    private String recurseFrontParaOld;
    private String recurseBehindParaOld;
    private int recurseFuncType; // 0:single 1:double
    private String coe1;
    private String coe2;
    private String frontPara1;
    private String behindPara1;
    private String frontPara2;
    private String behindPara2;
    private String signal;
    private String exprFormula;
    private String selDefFunc1;
    private String selDefFunc2;
    private final HashMap<String,String> selDefFrontParaOlds = new HashMap<>();
    private final HashMap<String,String> selDefBehindParaOlds = new HashMap<>();
    private final HashMap<String,Integer> selDefFuncTypes = new HashMap<>();

    public Func(String in0, String in1, String in2, String input1, String input2) {
        if (in0 != null && in1 != null && in2 != null) {
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
            processRecurseFuncString();
            getRecurseProperties();
            setMap();
        }
        selDefFunc1 = input1;
        selDefFunc2 = input2;
        processSelDefFuncString();
        getSelDefProperties();
    }

    public void processRecurseFuncString() {
        StringProcessor stringProcessor1 = new StringProcessor(funcn);
        stringProcessor1.process();
        this.funcn = stringProcessor1.getString();

        StringProcessor stringProcessor2 = new StringProcessor(func0);
        stringProcessor2.process();
        this.func0 = stringProcessor2.getString();

        StringProcessor stringProcessor3 = new StringProcessor(func1);
        stringProcessor3.process();
        this.func1 = stringProcessor3.getString();
    }

    public void processSelDefFuncString() {
        if (selDefFunc1 != null) {
            StringProcessor stringProcessor1 = new StringProcessor(selDefFunc1);
            stringProcessor1.process();
            this.selDefFunc1 = stringProcessor1.getString();
        }

        if (selDefFunc2 != null) {
            StringProcessor stringProcessor2 = new StringProcessor(selDefFunc2);
            stringProcessor2.process();
            this.selDefFunc2 = stringProcessor2.getString();
        }
    }

    public void getRecurseProperties() {
        Lexer lexer = new Lexer(funcn);
        for (int i = 0; i < 5; i++) {
            lexer.next();
        }
        this.recurseFrontParaOld = lexer.peek();
        lexer.next();
        if (lexer.peek().equals(",")) {
            this.recurseFuncType = 1;
            lexer.next();
            this.recurseBehindParaOld = lexer.peek();
            lexer.next();
        }
        else {
            this.recurseFuncType = 0;
            this.recurseBehindParaOld = null;
        }
        lexer.next();
        lexer.next(); //=
        coe1 = getCoe(lexer);
        for (int i = 0; i < 9; i++) {
            lexer.next(); //*f{n-1}(
        }
        if (recurseFuncType == 0) {
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
        if (recurseFuncType == 0) {
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

    public void getSelDefProperties() {
        if (selDefFunc1 != null) {
            Lexer lexer1 = new Lexer(selDefFunc1);
            getSelDef(lexer1);
        }
        if (selDefFunc2 != null) {
            Lexer lexer2 = new Lexer(selDefFunc2);
            getSelDef(lexer2);
        }
    }

    public void getSelDef(Lexer lexer) {
        String label = lexer.peek();
        lexer.next();
        lexer.next(); //skip '('
        selDefFrontParaOlds.put(label, lexer.peek());
        lexer.next();
        if (lexer.peek().equals(",")) {
            selDefFuncTypes.put(label,1);
            lexer.next();
            selDefBehindParaOlds.put(label, lexer.peek());
            lexer.next();
        }
        else {
            selDefFuncTypes.put(label,0);
            selDefBehindParaOlds.put(label,null);
        }
        lexer.next(); //skip ')'
        lexer.next(); //skip '='
        String expr = getExprFormula(lexer);
        if (label.equals("g")) {
            functions.put(7, expr);
        }
        else {
            functions.put(8, expr);
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
            else if (lexer.peek().equals(",") && flag == 1) {
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
        if (recurseFuncType == 0) {
            String fnSub1 = functions.get(n - 1);
            String fnSub2 = functions.get(n - 2);
            String tempSub1 = fnSub1.replace(recurseFrontParaOld, "(" + frontPara1 + ")");
            String tempSub2 = fnSub2.replace(recurseFrontParaOld, "(" + frontPara2 + ")");
            String fn = coe1 + "*(" + tempSub1 + ")" + signal + coe2 + "*(" + tempSub2 + ")";
            if (exprFormula != null) {
                fn = fn + exprFormula;
            }
            functions.put(n,fn);
        }
        else {
            String fnSub1 = functions.get(n - 1);
            String tempSub1 = fnSub1.replace(recurseFrontParaOld, "a");  // first bug
            tempSub1 = tempSub1.replace(recurseBehindParaOld, "b");
            tempSub1 = tempSub1.replace("a", "(" + frontPara1 + ")");
            tempSub1 = tempSub1.replace("b", "(" + behindPara1 + ")");
            String fnSub2 = functions.get(n - 2);
            String tempSub2 = fnSub2.replace(recurseFrontParaOld, "a");
            tempSub2 = tempSub2.replace(recurseBehindParaOld, "b");
            tempSub2 = tempSub2.replace("a", "(" + frontPara2 + ")");
            tempSub2 = tempSub2.replace("b", "(" + behindPara2 + ")");
            String fn = coe1 + "*(" + tempSub1 + ")" + signal + coe2 + "*(" + tempSub2 + ")";
            if (exprFormula != null) {
                fn = fn + exprFormula;
            }
            functions.put(n,fn);
        }
    }

    public String getFunction(int num) {
        return functions.get(num);
    }

    public String getFuncn() {
        return funcn;
    }

    public String getFunc0() {
        return func0;
    }

    public int getRecurseFuncType() {
        return recurseFuncType;
    }

    public String getFunc1() {
        return func1;
    }

    public String getRecurseFrontParaOld() {
        return recurseFrontParaOld;
    }

    public String getRecurseBehindParaOld() {
        return recurseBehindParaOld;
    }

    public String getSelDefFrontParaOld(String label) {
        return selDefFrontParaOlds.get(label);
    }

    public String getSelDefBehindParaOld(String label) {
        return selDefBehindParaOlds.get(label);
    }

    public int getSelDefType(String label) {
        return selDefFuncTypes.get(label);
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

    public String getSelDefFunc1() {
        return selDefFunc1;
    }

    public String getSelDefFunc2() {
        return selDefFunc2;
    }
}
