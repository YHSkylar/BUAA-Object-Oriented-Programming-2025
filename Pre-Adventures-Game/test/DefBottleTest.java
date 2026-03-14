import org.junit.Test;

import static org.junit.Assert.*;

public class DefBottleTest {

    @Test
    public void useBottle() {
        DefBottle bottle =new DefBottle(1,"a",102,3);
        int a = bottle.useBottle(5);
        assertEquals(true,bottle.getIfEmpty());
        assertEquals(a,9);
    }
}