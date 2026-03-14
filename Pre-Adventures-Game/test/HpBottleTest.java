import org.junit.Test;

import static org.junit.Assert.*;

public class HpBottleTest {

    @Test
    public void useBottle() {
        HpBottle bottle = new HpBottle(1,"a",2,0);
        int a = bottle.useBottle(5);
        assertEquals(true,bottle.getIfEmpty());
        assertEquals(a,7);
    }
}