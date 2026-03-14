import org.junit.Test;

import static org.junit.Assert.*;

public class BladeTest {

    @Test
    public void attack() {
        Blade blade = new Blade(101,"abc",10,50);
        int a = blade.attack(505,10,20);
        assertEquals(a,445);
    }
}