import org.junit.Test;

import static org.junit.Assert.*;

public class FragmentTest {

    @Test
    public void getId() {
        Fragment fragment = new Fragment(101,"a");
        assertEquals(fragment.getId(),101);
    }

    @Test
    public void getName() {
        Fragment fragment = new Fragment(101,"a");
        assertEquals(fragment.getName(),"a");
    }
}