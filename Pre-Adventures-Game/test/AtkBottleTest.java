import org.junit.Test;

import static org.junit.Assert.*;

public class AtkBottleTest {

    @Test
    public void useBottle() {
        AtkBottle bottle = new AtkBottle(1,"a",2,3);
        int a = bottle.useBottle(1);
        assertEquals(true,bottle.getIfEmpty());
        assertEquals(a,4);
    }
}