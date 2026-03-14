import org.junit.Test;

import static org.junit.Assert.*;

public class FuncTest {


    @Test
    public void getPara2() {
    }

    @Test
    public void getString() {
        String input = "f{n}(x,y)=0*f{n-1}(sin(x),sin(y))+35*f{n-2}(x,y^2)";
        String expected = "sin(y)";
        String in1 = "f{0}(x,y)=x-y";
        String in2 = "f{1}(x,y)=x^3+y";
        Lexer lexer = new Lexer("sin(y))+35*f{n-2}(x,y^2)");
        assertEquals(expected,new Func(in1,in2,input).getString(lexer));
        lexer.next();
        assertEquals(lexer.peek(),"+");
    }

    @Test
    public void processFuncString() {
        String input = "f{1}(x, y) = x^3 + y";
        String expected = "f{n}(x,y)=0*f{n-1}(x,y)+35*f{n-2}(x,y^2)";
        String in1 = "f{0}(x, y) = x - y";
        String in2 = "f{n}(x, y) = 0*f{n-1}(x, y) + 35*f{n-2}(x, y^2)";
        assertEquals(expected,new Func(in1,in2,input).getFuncn());
    }

    @Test
    public void getExprFormula() {
        String input = "f{n}(x,y)=0*f{n-1}(sin(x),sin(y))+35*f{n-2}(x,y^2)\n";
        String expected = "sin(y))+35*f{n-2}(x,y^2)";
        String in1 = "f{0}(x,y)=x-y\n";
        String in2 = "f{1}(x,y)=x^3+y\n";
        Lexer lexer = new Lexer("sin(y))+35*f{n-2}(x,y^2)\n");
        assertEquals(expected,new Func(in1,in2,input).getExprFormula(lexer));
    }

    @Test
    public void getProperties() {
        String input = "f{n}(x)=2*f{n-1}(x)-1*f{n-2}(x)+12";
        String in1 = "f{0}(x)=x";
        String in2 = "f{1}(x)=x^2";
        Func func = new Func(in1,in2,input);
        func.getProperties();
        assertEquals("f{0}(x)=x",func.getFunc0());
        assertEquals("f{1}(x)=x^2",func.getFunc1());
        assertEquals("f{n}(x)=2*f{n-1}(x)-1*f{n-2}(x)+12",func.getFuncn());
        assertEquals("x",func.getFrontParaOld());
        assertNull(func.getBehindParaOld());
        assertEquals("2",func.getCoe1());
        assertEquals(0,func.getType());
        assertEquals("x",func.getFrontPara1());
        assertEquals("-",func.getSignalOut());
        assertEquals("1",func.getCoe2());
        assertEquals("x",func.getFrontPara2());
        assertEquals("+1",func.getExpr());
    }

    @Test
    public void setMap() {
        String in0 = "f{0}(x)=x";
        String in1 = "f{1}(x)=x^2";
        String in2 = "f{n}(x)=2*f{n-1}(x)-1*f{n-2}(x)";
        Func func = new Func(in0,in1,in2);
        func.setMap();
    }
}