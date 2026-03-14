import org.junit.Test;

import static org.junit.Assert.*;

public class AxeTest {

    @Test
    public void attack() {
        Axe axe = new Axe(101,"abc",10,50);
        int a = axe.attack(505,10,10);
        assertEquals(a,50);
    }
}