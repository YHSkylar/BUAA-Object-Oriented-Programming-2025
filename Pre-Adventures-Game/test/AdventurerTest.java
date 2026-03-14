import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
public class AdventurerTest {

    @Test
    public void getId() {
        Adventurer adventurer = new Adventurer(1,"a");
        assertEquals(1,adventurer.getId());
    }

    @Test
    public void getName() {
        Adventurer adventurer = new Adventurer(1,"a");
        assertEquals("a",adventurer.getName());
    }

    @Test
    public void getHitPoint() {
        Adventurer adventurer = new Adventurer(1,"a");
        assertEquals(500,adventurer.getHitPoint());
    }

    @Test
    public void getAtk() {
        Adventurer adventurer = new Adventurer(1,"a");
        assertEquals(1,adventurer.getAtk());
    }

    @Test
    public void getDef() {
        Adventurer adventurer = new Adventurer(1,"a");
        assertEquals(0,adventurer.getDef());
    }

    @Test
    public void getCE() {
        Adventurer adventurer = new Adventurer(1,"a");
        assertEquals(1,adventurer.getCE());
    }

    @Test
    public void do_item_inW() {
        Adventurer adventurer = new Adventurer(123,"abc");
        Item item = new Item(1,"a",2);
        adventurer.add_item(1,item);
        assertEquals(item,adventurer.findItem_inWirehouse(1));
        adventurer.del_item_inW(1);
        assertNull(adventurer.findItem_inWirehouse(1));
        Equipment equipment = new Equipment(2,"b",5,6);
        adventurer.add_item(2,equipment);
        adventurer.add_equipmentDurability_inW(2);
        assertEquals(6,equipment.getDurability());
    }

    @Test
    public void take_item_do_item_inB() {
        Adventurer adventurer = new Adventurer(123,"abc");
        Item item = new Equipment(1,"a",2,10);
        adventurer.add_item(1,item);
        adventurer.take_item(1);
        assertEquals(item,adventurer.findItem_inBackpack(1));
        assertTrue(adventurer.ifContain_inB(1));
        assertFalse(adventurer.ifContain_inB(2));
        adventurer.del_item_inB(1);
        assertNull(adventurer.findItem_inBackpack(1));
    }

    @Test
    public void useBottle() {
        Adventurer adventurer = new Adventurer(123,"abc");
        HpBottle hpBottle = new HpBottle(1,"hp",1,0);
        AtkBottle atkBottle = new AtkBottle(2,"atk",2,5);
        DefBottle defBottle = new DefBottle(3,"c",3,5);
        adventurer.add_item(1,hpBottle);
        adventurer.add_item(2,atkBottle);
        adventurer.add_item(3,defBottle);
        adventurer.take_item(1);
        adventurer.take_item(2);
        adventurer.take_item(3);
        adventurer.useBottle(1);
        adventurer.useBottle(2);
        adventurer.useBottle(3);
        assertEquals(501,adventurer.getHitPoint());
        assertEquals(6,adventurer.getAtk());
        assertEquals(5,adventurer.getDef());
    }

    @Test
    public void useFragment() {
        Adventurer adventurer = new Adventurer(1,"abc");
        Fragment fragment1 = new Fragment(101,"a");
        Fragment fragment2 = new Fragment(102,"a");
        Fragment fragment3 = new Fragment(103,"a");
        Fragment fragment4 = new Fragment(104,"a");
        Fragment fragment5 = new Fragment(105,"a");
        Fragment fragment6 = new Fragment(106,"a");

        adventurer.addFragment("a",fragment1);
        assertEquals(adventurer.getFragments("a").get(0),fragment1);
        adventurer.addFragment("a",fragment2);
        adventurer.addFragment("a",fragment3);
        adventurer.addFragment("a",fragment4);
        Boolean a = adventurer.whetherUseFragment("a");
        assertFalse(a);
        adventurer.addFragment("a",fragment5);
        adventurer.addFragment("a",fragment6);
        assertEquals(adventurer.getFragmentNameSize("a"),6);
        a = adventurer.whetherUseFragment("a");
        assertTrue(a);

        char x = adventurer.useFragment("a",56);
        assertEquals(x,'c');
        assertEquals(adventurer.findItem_inWirehouse(56).getName(),"a");

        Fragment fragment7 = new Fragment(107,"a");
        Fragment fragment8 = new Fragment(108,"a");
        Fragment fragment9 = new Fragment(109,"a");
        Fragment fragment10 = new Fragment(110,"a");
        Fragment fragment11 = new Fragment(111,"a");
        adventurer.addFragment("a",fragment7);
        adventurer.addFragment("a",fragment8);
        adventurer.addFragment("a",fragment9);
        adventurer.addFragment("a",fragment10);
        adventurer.addFragment("a",fragment11);

        Bottle bottle = new Bottle(56,"bottle",10,5);
        bottle.setEmpty();
        adventurer.add_item(56,bottle);
        x = adventurer.useFragment("a",56);
        assertEquals(x,'a');
        Item item1 = adventurer.findItem_inWirehouse(56);
        if (item1 instanceof Bottle) assertFalse(((Bottle) item1).getIfEmpty());

        Fragment fragment12 = new Fragment(112,"a");
        Fragment fragment13 = new Fragment(113,"a");
        Fragment fragment14 = new Fragment(114,"a");
        Fragment fragment15 = new Fragment(115,"a");
        Fragment fragment16 = new Fragment(116,"a");
        adventurer.addFragment("a",fragment12);
        adventurer.addFragment("a", fragment13);
        adventurer.addFragment("a", fragment14);
        adventurer.addFragment("a", fragment15);
        adventurer.addFragment("a", fragment16);

        Equipment equipment = new Equipment(56,"equ",10,5);
        adventurer.add_item(56,equipment);
        x = adventurer.useFragment("a",56);
        assertEquals(x,'b');
        Item item2 = adventurer.findItem_inWirehouse(56);
        if (item2 instanceof Equipment) assertEquals(11,((Equipment) item2).getDurability());
    }

    @Test
    public void beAttacked() {
        Axe axe = new Axe(101,"axe",10,50);
        Sword sword = new Sword(102,"sword",10,5);
        Blade blade = new Blade(103,"blade",10,5);
        Adventurer adventurer = new Adventurer(1,"abc");
        adventurer.beAttacked(axe,20);
        assertEquals(50,adventurer.getHitPoint());
        adventurer.beAttacked(sword,1);
        assertEquals(44,adventurer.getHitPoint());
        adventurer.beAttacked(blade,1);
        assertEquals(38,adventurer.getHitPoint());
    }

    @Test
    public void useEquipment() {
        Adventurer adventurer = new Adventurer(1,"abc");
        Equipment equipment = new Equipment(101,"a",2,5);
        adventurer.useEquipment(equipment);
        assertEquals(equipment.getDurability(),1);
        adventurer.useEquipment(equipment);
        assertNull(adventurer.findItem_inWirehouse(1));
    }

    @Test
    public void testHire() {
        Adventurer adventurer1 = new Adventurer(1,"a");
        Adventurer adventurer2 = new Adventurer(2,"b");
        adventurer1.hire(adventurer2);
        ArrayList<Adventurer> arrayList1 = adventurer1.getEmployees();
        assertEquals(1,adventurer1.getEmployeesSize());
        assertEquals(adventurer2, arrayList1.get(0));
        adventurer1.removeEmployee(2);
        assertEquals(0,adventurer1.getEmployeesSize());
    }
    @Test
    public void testBeHired() {
        Adventurer adventurer1 = new Adventurer(1,"a");
        Adventurer adventurer2 = new Adventurer(2,"b");
        adventurer2.beHired(adventurer1);
    }

    public void testHelp() {

    }

    @Test
    public void testGetTotalCe() {
        Adventurer adventurer1 = new Adventurer(1,"a");
        Adventurer adventurer2 = new Adventurer(2,"b");
        Adventurer adventurer3 = new Adventurer(3,"c");
        Axe axe = new Axe(101,"axe",10,5);
        adventurer1.add_item(101,axe);
        adventurer1.take_item(101);
        adventurer1.hire(adventurer2);
        adventurer1.hire(adventurer3);
        assertEquals(adventurer1.getTotalCe(),8);
        adventurer1.addAllDef(30);
        assertEquals(adventurer1.getTotalCe(),98);
        adventurer1.addAllAtk(40);
        assertEquals(adventurer1.getTotalCe(),218);
    }

}