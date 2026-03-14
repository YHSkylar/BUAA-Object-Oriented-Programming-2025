import org.junit.Test;

import static org.junit.Assert.*;

public class SwordTest {

    @Test
    public void attack() {
        Sword sword = new Sword(101,"abc",10,50);
        int a = sword.attack(505,10,20);
        assertEquals(a,465);
    }
}