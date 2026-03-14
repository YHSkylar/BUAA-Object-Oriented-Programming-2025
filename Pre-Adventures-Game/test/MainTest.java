import org.junit.Test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
public class MainTest {

    @Test
    public void main() {
    }

    @Test
    public void add_adventurer() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("1");
        strings.add("Alice");

        HashMap<Integer, Adventurer> adventurers = new HashMap<>();

        Main.add_adventurer(strings,adventurers);

        assertTrue(adventurers.containsKey(1));
        Adventurer addedAdventurer = adventurers.get(1);
        assertEquals("Alice", addedAdventurer.getName());
    }

    @Test
    public void add_take_del_adventurers_bottle() {
        Adventurer adventurer = new Adventurer(1, "Alice");
        HashMap<Integer, Adventurer> adventurers = new HashMap<>();
        adventurers.put(adventurer.getId(), adventurer);

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("add_adventurers_bottle");  //1
        strings1.add("1"); // adventurer id
        strings1.add("101"); // bottle id
        strings1.add("Hp"); // bottle name
        strings1.add("50"); // bottle capacity
        strings1.add("HpBottle"); // bottle type
        strings1.add("0"); // bottle ce

        ArrayList<String> strings2 = new ArrayList<>();
        strings2.add("add_adventurers_bottle");  //1
        strings2.add("1"); // adventurer id
        strings2.add("102"); // bottle id
        strings2.add("Atk"); // bottle name
        strings2.add("50"); // bottle capacity
        strings2.add("AtkBottle"); // bottle type
        strings2.add("20"); // bottle ce

        ArrayList<String> strings3 = new ArrayList<>();
        strings3.add("add_adventurers_bottle");  //1
        strings3.add("1"); // adventurer id
        strings3.add("103"); // bottle id
        strings3.add("Def"); // bottle name
        strings3.add("50"); // bottle capacity
        strings3.add("DefBottle"); // bottle type
        strings3.add("20"); // bottle ce

        ArrayList<String> strings4 = new ArrayList<>();
        strings4.add("add_adventurers_bottle");  //1
        strings4.add("1"); // adventurer id
        strings4.add("104"); // bottle id
        strings4.add("Hp"); // bottle name
        strings4.add("50"); // bottle capacity
        strings4.add("HpBottle"); // bottle type
        strings4.add("0"); // bottle ce
        // 调用方法
        Main.add_adventurers_bottle(strings1, adventurers);
        Main.add_adventurers_bottle(strings2, adventurers);
        Main.add_adventurers_bottle(strings3, adventurers);
        Main.add_adventurers_bottle(strings4, adventurers);
        // 验证结果
        Item bottle1 = adventurers.get(1).findItem_inWirehouse(101);
        assertNotNull(bottle1);
        assertTrue(bottle1 instanceof HpBottle);
        assertEquals("Hp", bottle1.getName());
        assertEquals(50, ((HpBottle) bottle1).getCapacity());
        assertEquals(0, bottle1.getCe());

        Item bottle2 = adventurers.get(1).findItem_inWirehouse(102);
        assertNotNull(bottle2);
        assertTrue(bottle2 instanceof AtkBottle);
        assertEquals("Atk", bottle2.getName());
        assertEquals(50, ((AtkBottle) bottle2).getCapacity());
        assertEquals(20,  bottle2.getCe());

        Item bottle3 = adventurers.get(1).findItem_inWirehouse(103);
        assertNotNull(bottle3);
        assertTrue(bottle3 instanceof DefBottle);
        assertEquals("Def", bottle3.getName());
        assertEquals(50, ((DefBottle) bottle3).getCapacity());
        assertEquals(20, bottle3.getCe());

        Item bottle4 = adventurers.get(1).findItem_inWirehouse(104);
        assertNotNull(bottle4);
        assertTrue(bottle4 instanceof HpBottle);
        assertEquals("Hp", bottle4.getName());
        assertEquals(50, ((HpBottle) bottle4).getCapacity());
        assertEquals(0, bottle4.getCe());

        ArrayList<String> strings5 = new ArrayList<>();
        strings5.add("take_del");
        strings5.add("1");
        strings5.add("101");

        Main.take_item(strings5,adventurers);
        assertTrue(adventurers.get(1).ifContain_inB(101));

        ArrayList<String> strings6 = new ArrayList<>();
        strings6.add("take_del");
        strings6.add("1");
        strings6.add("102");

        Main.take_item(strings6,adventurers);
        assertTrue(adventurers.get(1).ifContain_inB(102));

        ArrayList<String> strings7 = new ArrayList<>();
        strings7.add("take_del");
        strings7.add("1");
        strings7.add("103");

        Main.take_item(strings7,adventurers);
        assertTrue(adventurers.get(1).ifContain_inB(103));

        ArrayList<String> strings8 = new ArrayList<>();
        strings8.add("take_del");
        strings8.add("1");
        strings8.add("104");

        Main.take_item(strings8,adventurers);
        assertFalse(adventurers.get(1).ifContain_inB(104));

        Main.del_item(strings5, adventurers);
        assertNull(adventurers.get(1).findItem_inWirehouse(101));

        Main.del_item(strings6, adventurers);
        assertNull(adventurers.get(1).findItem_inWirehouse(102));

        Main.del_item(strings7, adventurers);
        assertNull(adventurers.get(1).findItem_inWirehouse(103));

        Main.del_item(strings8, adventurers);
        assertNull(adventurers.get(1).findItem_inWirehouse(104));
    }

    @Test
    public void add_del_adventurers_equipment() {
        Adventurer adventurer = new Adventurer(1, "Alice");
        HashMap<Integer, Adventurer> adventurers = new HashMap<>();
        adventurers.put(adventurer.getId(), adventurer);

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("add_equ");
        strings1.add("1");
        strings1.add("101");
        strings1.add("Axe");
        strings1.add("50");
        strings1.add("Axe");
        strings1.add("20");

        ArrayList<String> strings2 = new ArrayList<>();
        strings2.add("add_equ");
        strings2.add("1");
        strings2.add("102");
        strings2.add("Sword");
        strings2.add("60");
        strings2.add("Sword");
        strings2.add("30");

        ArrayList<String> strings3 = new ArrayList<>();
        strings3.add("add_equ");
        strings3.add("1");
        strings3.add("103");
        strings3.add("Blade");
        strings3.add("70");
        strings3.add("Blade");
        strings3.add("40");

        ArrayList<String> strings4 = new ArrayList<>();
        strings4.add("add_equ");
        strings4.add("1");
        strings4.add("104");
        strings4.add("Blade");
        strings4.add("80");
        strings4.add("Blade");
        strings4.add("50");

        Main.add_adventurers_equipment(strings1,adventurers);
        Main.add_adventurers_equipment(strings2,adventurers);
        Main.add_adventurers_equipment(strings3,adventurers);
        Main.add_adventurers_equipment(strings4,adventurers);

        Item equipment1 = adventurers.get(1).findItem_inWirehouse(101);
        assertNotNull(equipment1);
        assertTrue(equipment1 instanceof Axe);
        assertEquals("Axe", equipment1.getName());
        assertEquals(50,((Equipment) equipment1).getDurability());
        assertEquals(20, equipment1.getCe());

        Item equipment2 = adventurers.get(1).findItem_inWirehouse(102);
        assertNotNull(equipment2);
        assertTrue(equipment2 instanceof Sword);
        assertEquals("Sword", equipment2.getName());
        assertEquals(60,((Equipment) equipment2).getDurability());
        assertEquals(30, equipment2.getCe());

        Item equipment3 = adventurers.get(1).findItem_inWirehouse(103);
        assertNotNull(equipment3);
        assertTrue(equipment3 instanceof Blade);
        assertEquals("Blade", equipment3.getName());
        assertEquals(70,((Equipment) equipment3).getDurability());
        assertEquals(40, equipment3.getCe());

        ArrayList<String> strings5 = new ArrayList<>();
        strings5.add("add_dur");
        strings5.add("1");
        strings5.add("101");

        Main.add_equipment_durability(strings5,adventurers);

        assertEquals(51,((Equipment) equipment1).getDurability());
        ArrayList<String> strings6 = new ArrayList<>();
        strings6.add("take_del");
        strings6.add("1");
        strings6.add("101");

        ArrayList<String> strings7 = new ArrayList<>();
        strings7.add("take_del");
        strings7.add("1");
        strings7.add("102");

        ArrayList<String> strings8 = new ArrayList<>();
        strings8.add("take_del");
        strings8.add("1");
        strings8.add("103");

        ArrayList<String> strings9 = new ArrayList<>();
        strings9.add("take_del");
        strings9.add("1");
        strings9.add("104");

        Main.take_item(strings6,adventurers);
        Main.take_item(strings7,adventurers);
        Main.take_item(strings8,adventurers);
        assertTrue(adventurers.get(1).ifContain_inB(103));
        Main.take_item(strings9,adventurers);
        assertTrue(adventurers.get(1).ifContain_inB(104));
        assertFalse(adventurers.get(1).ifContain_inB(103));

        Item item1 = adventurer.findEquipmentInBackpack("Axe");
        assertEquals(item1,equipment1);
        Item item2 = adventurer.findEquipmentInBackpack("cd");
        assertNull(item2);

        Main.del_item(strings6,adventurers);
        Main.del_item(strings7,adventurers);
        Main.del_item(strings9,adventurers);
        assertNull(adventurers.get(1).findItem_inWirehouse(104));
    }

    @Test
    public void use_bottle() {
        Adventurer adventurer = new Adventurer(1, "Alice");
        HashMap<Integer, Adventurer> adventurers = new HashMap<>();
        adventurers.put(adventurer.getId(), adventurer);

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("use_bottle");
        strings1.add("1");
        strings1.add("101");

        HpBottle bottle = new HpBottle(101,"a",10,0);
        adventurer.add_item(101,bottle);
        Main.use_bottle(strings1,adventurers);

        adventurer.take_item(101);
        Main.use_bottle(strings1,adventurers);

        Main.use_bottle(strings1,adventurers);
    }

    @Test
    public void get_fragment() {
        Adventurer adventurer = new Adventurer(1, "Alice");
        HashMap<Integer, Adventurer> adventurers = new HashMap<>();
        adventurers.put(adventurer.getId(), adventurer);

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("get_fragment");
        strings1.add("1");
        strings1.add("101");
        strings1.add("a");

        Main.get_fragment(strings1,adventurers);
    }

    @Test
    public void fragment_exchange() {
        Adventurer adventurer = new Adventurer(1, "Alice");
        HashMap<Integer, Adventurer> adventurers = new HashMap<>();
        adventurers.put(adventurer.getId(), adventurer);

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("fra_exchange");
        strings1.add("1");
        strings1.add("a");
        strings1.add("56");//welfareId

        Fragment fragment1 = new Fragment(101,"a");
        Fragment fragment2 = new Fragment(102,"a");
        Fragment fragment3 = new Fragment(103,"a");
        Fragment fragment4 = new Fragment(104,"a");
        Fragment fragment5 = new Fragment(105,"a");
        Fragment fragment6 = new Fragment(106,"a");
        adventurer.addFragment("a",fragment1);
        Main.fragment_exchange(strings1,adventurers);//兑换失败

        adventurer.addFragment("a",fragment2);
        adventurer.addFragment("a",fragment3);
        adventurer.addFragment("a",fragment4);
        adventurer.addFragment("a",fragment5);
        Main.fragment_exchange(strings1,adventurers);

        adventurer.addFragment("a",fragment1);
        adventurer.addFragment("a",fragment2);
        adventurer.addFragment("a",fragment3);
        adventurer.addFragment("a",fragment4);
        adventurer.addFragment("a",fragment5);
        Bottle bottle = new Bottle(56,"bottle",10,1);
        adventurer.add_item(56,bottle);
        Main.fragment_exchange(strings1,adventurers);
        adventurer.del_item_inW(56);

        adventurer.addFragment("a",fragment1);
        adventurer.addFragment("a",fragment2);
        adventurer.addFragment("a",fragment3);
        adventurer.addFragment("a",fragment4);
        adventurer.addFragment("a",fragment5);
        Equipment equipment = new Equipment(56,"equ",10,1);
        adventurer.add_item(56,equipment);
        Main.fragment_exchange(strings1,adventurers);

    }

    @Test
    public void fight() {
        Adventurer adventurer1 = new Adventurer(1, "Alice");
        Adventurer adventurer2 = new Adventurer(2, "Peach");
        Adventurer adventurer3 = new Adventurer(3, "Tom");
        HashMap<Integer, Adventurer> adventurers = new HashMap<>();
        adventurers.put(adventurer1.getId(), adventurer1);
        adventurers.put(adventurer2.getId(), adventurer2);
        adventurers.put(adventurer3.getId(), adventurer3);

        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("fight");
        strings1.add("1");
        strings1.add("a");//装备name
        strings1.add("normal");
        strings1.add("2");
        strings1.add("2");
        strings1.add("3");

        Axe axe = new Axe(101,"a",10,5);
        adventurer1.add_item(101,axe);
        Main.fight(strings1,adventurers);

        adventurer1.take_item(101);
        Main.fight(strings1,adventurers);

        DefBottle defBottle = new DefBottle(201,"defb",10,100);
        adventurer2.add_item(201,defBottle);
        adventurer2.take_item(201);
        adventurer2.useBottle(201);
        Main.fight(strings1,adventurers);

        Axe axe1 = new Axe(102,"b",10,1000);
        adventurer1.add_item(102,axe1);
        adventurer1.take_item(102);

        ArrayList<String> strings2 = new ArrayList<>();
        strings2.add("fight");
        strings2.add("1");
        strings2.add("b");//装备name
        strings2.add("chain");
        strings2.add("2");
        strings2.add("2");
        strings2.add("3");
        Main.fight(strings2,adventurers);
    }
}