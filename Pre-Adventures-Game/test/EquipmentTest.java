import org.junit.Test;

import static org.junit.Assert.*;

public class EquipmentTest {

    @Test
    public void getDurability() {
        int id = 1;
        String name = "a";
        int durability = 5;
        int ce = 1;
        Equipment equipment = new Equipment(id,name,durability,ce);
        assertEquals(durability,equipment.getDurability());
    }

    @Test
    public void add_dur() {
        int durability = 5;
        Equipment equipment = new Equipment(1,"A",durability,1);
        equipment.add_dur();
        assertEquals(6,equipment.getDurability());
    }

    @Test
    public void attack() {
        Equipment equipment = new Equipment(101,"abc",10,50);
        int a = equipment.attack(505,10,20);
        assertEquals(a,505);
    }

    @Test
    public void useEquipment() {
        Equipment equipment = new Equipment(101,"abc",10,50);
        equipment.useEquipment();
        assertEquals(equipment.getDurability(),9);
    }
}