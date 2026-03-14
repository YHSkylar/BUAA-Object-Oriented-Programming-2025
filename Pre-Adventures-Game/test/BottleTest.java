import org.junit.Test;

import static org.junit.Assert.*;

public class BottleTest {

    @Test
    public void getCapacity() {
        int capacity = 0;
        Bottle bottle = new Bottle(1,"a",capacity,5);
        assertEquals(capacity,bottle.getCapacity());
    }

    @Test
    public void useBottle() {
        Bottle bottle = new Bottle(1,"a",1,1);
        int a = bottle.useBottle(5);
        assertEquals(5,a);
        assertEquals(bottle.getIfEmpty(),true);
    }

    @Test
    public void getIfEmpty() {
        Bottle bottle = new Bottle(1,"a",1,1);
        assertEquals(false,bottle.getIfEmpty());
    }

    @Test
    public void setEmpty_Full() {
        Bottle bottle = new Bottle(1,"A",1,1);
        bottle.setEmpty();
        assertEquals(true,bottle.getIfEmpty());
        bottle.setFull();
        assertEquals(false,bottle.getIfEmpty());
    }

}