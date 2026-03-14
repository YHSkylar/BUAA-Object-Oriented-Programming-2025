import org.junit.Test;

import static org.junit.Assert.*;


public class FuncTest {

    @Test
    public void getSelDefProperties() {
        String input1 = "g(x,y) = x^5 + sin(x)";
        String input2 = "h(x,y) = g(0,6) + 123";
        Func func = new Func(null,null,null, input1, input2);
        assertEquals(func.getSelDefFunc1(),"g(x,y)=x^5+sin(x)");
        assertEquals(func.getSelDefFunc2(),"h(x,y)=g(0,6)+123");
        assertEquals(func.getSelDefType("g"),1);
        assertEquals(func.getSelDefType("h"),1);
        assertEquals(func.getSelDefFrontParaOld("g"),"x");
        assertEquals(func.getSelDefBehindParaOld("g"),"y");
        assertEquals(func.getFunction(7),"x^5+sin(x)");
        assertEquals(func.getFunction(8),"g(0,6)+123");
    }
}