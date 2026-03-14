import org.junit.Test;

import static org.junit.Assert.*;

public class ItemTest {

    @Test
    public void getId() {
        int id = 123;
        Item item = new Item(id,"abc",1);
        assertEquals(id,item.getId());
    }

    @Test
    public void getName() {
        String name = "abc";
        Item item = new Item(123,name,1);
        assertEquals(name,item.getName());
    }

    @Test
    public void getCE() {
        int ce = 1;
        Item item = new Item(123,"abc",ce);
        assertEquals(ce,item.getCe());
    }
}