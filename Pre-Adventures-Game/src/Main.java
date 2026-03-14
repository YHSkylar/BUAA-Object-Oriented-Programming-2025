import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ArrayList<ArrayList<String>> inputInfo = new ArrayList<>(); // 解析后的输入将会存进该容器中, 类似于c语言的二维数组
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim()); // 读取行数
        for (int i = 0; i < n; ++i) {
            String nextLine = scanner.nextLine(); // 读取本行指令
            String[] strings = nextLine.trim().split(" +"); // 按空格对行进行分割
            inputInfo.add(new ArrayList<>(Arrays.asList(strings))); // 将指令分割后的各个部分存进容器中
        }

        HashMap<Integer, Adventurer> adventurers = new HashMap<>();

        int op;
        int i;
        for (i = 0; i < n; i++) {
            op = Integer.parseInt(inputInfo.get(i).get(0));
            switch (op) {
                case 1:
                    add_adventurer(inputInfo.get(i), adventurers);
                    break;
                case 2:
                    add_adventurers_bottle(inputInfo.get(i), adventurers);
                    break;
                case 3:
                    add_adventurers_equipment(inputInfo.get(i), adventurers);
                    break;
                case 4:
                    add_equipment_durability(inputInfo.get(i), adventurers);
                    break;
                case 5:
                    del_item(inputInfo.get(i), adventurers);
                    break;
                case 6:
                    take_item(inputInfo.get(i), adventurers);
                    break;
                case 7:
                    use_bottle(inputInfo.get(i), adventurers);
                    break;
                case 8:
                    get_fragment(inputInfo.get(i), adventurers);
                    break;
                case 9:
                    fragment_exchange(inputInfo.get(i), adventurers);
                    break;
                case 10:
                    fight(inputInfo.get(i),adventurers);
                    break;
                case 11:
                    hire(inputInfo.get(i),adventurers);
                    break;
                case 12:
                    explore(inputInfo.get(i),adventurers);
                    break;
                default:
                    break;
            }
        }
    }

    public static void add_adventurer(ArrayList<String> strings,
                                      HashMap<Integer, Adventurer> adventurers) {
        int id = Integer.parseInt(strings.get(1));
        String name = strings.get(2);
        Adventurer adventurer = new Adventurer(id, name);
        adventurers.put(id, adventurer);
    }

    public static void add_adventurers_bottle(ArrayList<String> strings,
                                              HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int botId = Integer.parseInt(strings.get(2));
        String name = strings.get(3);
        int capacity = Integer.parseInt(strings.get(4));
        String type = strings.get(5);
        int ce = Integer.parseInt(strings.get(6));
        if (type.equals("HpBottle")) {
            Item bottle = new HpBottle(botId, name, capacity, ce);
            adventurers.get(advId).add_item(botId, bottle);
        } else if (type.equals("AtkBottle")) {
            Item bottle = new AtkBottle(botId, name, capacity, ce);
            adventurers.get(advId).add_item(botId, bottle);
        } else {
            Item bottle = new DefBottle(botId, name, capacity, ce);
            adventurers.get(advId).add_item(botId, bottle);
        }
    }

    public static void add_adventurers_equipment(ArrayList<String> strings,
                                                 HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int equId = Integer.parseInt(strings.get(2));
        String name = strings.get(3);
        int durability = Integer.parseInt(strings.get(4));
        String type = strings.get(5);
        int ce = Integer.parseInt(strings.get(6));
        if (type.equals("Axe")) {
            Item equipment = new Axe(equId, name, durability, ce);
            adventurers.get(advId).add_item(equId, equipment);
        } else if (type.equals("Sword")) {
            Item equipment = new Sword(equId, name, durability, ce);
            adventurers.get(advId).add_item(equId, equipment);
        } else {
            Item equipment = new Blade(equId, name, durability, ce);
            adventurers.get(advId).add_item(equId, equipment);
        }
    }

    public static void add_equipment_durability(ArrayList<String> strings,
                                                HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int equId = Integer.parseInt(strings.get(2));
        adventurers.get(advId).add_equipmentDurability_inW(equId); //修改仓库中的物品
        Item itemW = adventurers.get(advId).findItem_inWirehouse(equId);
        if (itemW instanceof Equipment) {
            String outName = itemW.getName();
            int outDur = ((Equipment) itemW).getDurability();
            System.out.println(outName + " " + outDur);
        }
    }

    public static void del_item(ArrayList<String> strings,
                                HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int id = Integer.parseInt(strings.get(2));
        Item item = adventurers.get(advId).findItem_inWirehouse(id);
        if (item instanceof HpBottle) {
            String type = "HpBottle";
            String outName = item.getName();
            int outCap = ((HpBottle) item).getCapacity();
            System.out.println(type + " " + outName + " " + outCap);
        } else if (item instanceof AtkBottle) {
            String type = "AtkBottle";
            String outName = item.getName();
            int outCap = ((AtkBottle) item).getCapacity();
            System.out.println(type + " " + outName + " " + outCap);
        } else if (item instanceof DefBottle) {
            String type = "DefBottle";
            String outName = item.getName();
            int outCap = ((DefBottle) item).getCapacity();
            System.out.println(type + " " + outName + " " + outCap);
        } else if (item instanceof Axe) {
            String type = "Axe";
            String outName = item.getName();
            int outDur = ((Equipment) item).getDurability();
            System.out.println(type + " " + outName + " " + outDur);
        } else if (item instanceof Sword) {
            String type = "Sword";
            String outName = item.getName();
            int outDur = ((Equipment) item).getDurability();
            System.out.println(type + " " + outName + " " + outDur);
        } else if (item instanceof Blade) {
            String type = "Blade";
            String outName = item.getName();
            int outDur = ((Equipment) item).getDurability();
            System.out.println(type + " " + outName + " " + outDur);
        }

        adventurers.get(advId).del_item_inW(id);

        if (adventurers.get(advId).ifContain_inB(id)) {
            adventurers.get(advId).del_item_inB(id);
        }
    }

    public static void take_item(ArrayList<String> strings,
                                 HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int id = Integer.parseInt(strings.get(2));
        if (!adventurers.get(advId).ifContain_inB(id)) {
            adventurers.get(advId).take_item(id);
        }
    }

    public static void use_bottle(ArrayList<String> strings,
                                  HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int botId = Integer.parseInt(strings.get(2));
        if (adventurers.get(advId).ifContain_inB(botId)) {
            Item item = adventurers.get(advId).findItem_inBackpack(botId);
            if (item instanceof Bottle) {
                if (!((Bottle) item).getIfEmpty()) {
                    adventurers.get(advId).useBottle(botId);
                    String name = adventurers.get(advId).getName();
                    int hitPoint = adventurers.get(advId).getHitPoint();
                    int atk = adventurers.get(advId).getAtk();
                    int def = adventurers.get(advId).getDef();
                    System.out.println(name + " " + hitPoint + " " + atk + " " + def);
                } else {
                    String name = adventurers.get(advId).getName();
                    int hitPoint = adventurers.get(advId).getHitPoint();
                    int atk = adventurers.get(advId).getAtk();
                    int def = adventurers.get(advId).getDef();
                    System.out.println(name + " " + hitPoint + " " + atk + " " + def);
                    adventurers.get(advId).del_item_inW(botId);
                    adventurers.get(advId).del_item_inB(botId);
                }
            }

        } else {
            String advName = adventurers.get(advId).getName();
            String name = adventurers.get(advId).findItem_inWirehouse(botId).getName();
            System.out.println(advName + " fail to use " + name);
        }
    }

    public static void get_fragment(ArrayList<String> strings,
                                    HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int fragId = Integer.parseInt(strings.get(2));
        String name = strings.get(3);
        Fragment fragment = new Fragment(fragId, name);
        adventurers.get(advId).addFragment(name, fragment);
    }

    public static void fragment_exchange(ArrayList<String> strings,
                                         HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        String name = strings.get(2);
        int welfareId = Integer.parseInt(strings.get(3));
        if (adventurers.get(advId).whetherUseFragment(name)) { //兑换成功
            char module = adventurers.get(advId).useFragment(name, welfareId);
            if (module == 'a' || module == 'b') {
                Item item = adventurers.get(advId).findItem_inWirehouse(welfareId);
                if (item instanceof Bottle) {
                    String bottleName = item.getName();
                    int bottleCapacity = ((Bottle) item).getCapacity();
                    System.out.println(bottleName + " " + bottleCapacity);
                } else if (item instanceof Equipment) {
                    String equipmentName = item.getName();
                    int equipmentDurability = ((Equipment) item).getDurability();
                    System.out.println(equipmentName + " " + equipmentDurability);
                }
            } else {
                System.out.println("Congratulations! HpBottle " + name + " acquired");
            }
        } else { //兑换失败
            int size = adventurers.get(advId).getFragmentNameSize(name);
            System.out.println(size + ": Not enough fragments collected yet");
        }
    }

    public static void fight(ArrayList<String> strings,
                             HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        String name = strings.get(2);
        String type = strings.get(3);
        if (type.equals("normal")) {
            normalFight(strings,adventurers,advId,name);
        }
        else {
            chainFight(strings,adventurers,advId,name);
        }
    }

    public static void normalFight(ArrayList<String> strings,
                                   HashMap<Integer, Adventurer> adventurers,int advId,String name) {
        int k = Integer.parseInt(strings.get(4));
        ArrayList<Integer> beAttackedAdventurers = new ArrayList<>();
        int allDef = 0;
        for (int i = 0; i < k; i++) {
            int id = Integer.parseInt(strings.get(5 + i));
            if (adventurers.get(id).getDef() > allDef) {
                allDef = adventurers.get(id).getDef();
            }
            beAttackedAdventurers.add(id);
        }

        Item item = adventurers.get(advId).findEquipmentInBackpack(name);
        if (item == null) { //未携带对应装备，失败
            System.out.println("Adventurer " + advId + " defeated");
        }
        else {
            if (item.getCe() + adventurers.get(advId).getAtk() <= allDef) {
                System.out.println("Adventurer " + advId + " defeated");
            }
            else {
                int atk = adventurers.get(advId).getAtk();
                for (int id : beAttackedAdventurers) {
                    adventurers.get(id).beAttacked(item,atk);
                    ArrayList<Adventurer> employees = adventurers.get(id).getEmployees();
                    for (Adventurer employee : employees) {
                        adventurers.get(employee.getId()).help(adventurers.get(id));
                    }
                    String namei = adventurers.get(id).getName();
                    int hitPoint = adventurers.get(id).getHitPoint();
                    System.out.println(namei + " " + hitPoint);
                }
                adventurers.get(advId).useEquipment(item);
            }
        }
    }

    public static void chainFight(ArrayList<String> strings,
                                  HashMap<Integer, Adventurer> adventurers,int advId,String name) {
        int k = Integer.parseInt(strings.get(4));
        ArrayList<Integer> beAttackedAdventurers = new ArrayList<>();
        int allDef = 0;
        for (int i = 0; i < k; i++) {
            int id = Integer.parseInt(strings.get(5 + i));
            if (!beAttackedAdventurers.contains(id)) {
                beAttackedAdventurers.add(id);
            }
            dfs(0,beAttackedAdventurers,adventurers.get(id));
        }
        for (int id : beAttackedAdventurers) {
            if (adventurers.get(id).getDef() > allDef) {
                allDef = adventurers.get(id).getDef();
            }
        }

        Item item = adventurers.get(advId).findEquipmentInBackpack(name);
        if (item == null) { //未携带对应装备，失败
            System.out.println("Adventurer " + advId + " defeated");
        }
        else {
            if (item.getCe() + adventurers.get(advId).getAtk() <= allDef) {
                System.out.println("Adventurer " + advId + " defeated");
            }
            else {
                int totalHarm = 0;
                int atk = adventurers.get(advId).getAtk();
                for (int id : beAttackedAdventurers) {
                    int hitPointBefore = adventurers.get(id).getHitPoint();
                    adventurers.get(id).beAttacked(item,atk);
                    int hitPointAfter = adventurers.get(id).getHitPoint();
                    totalHarm = totalHarm + hitPointBefore - hitPointAfter;
                }
                adventurers.get(advId).useEquipment(item);
                System.out.println(totalHarm);
            }
        }
    }

    public static void dfs(int dep,ArrayList<Integer> arrayList,Adventurer adventurer) {
        if (dep == 4) {
            return;
        }
        for (int i = 0; i < adventurer.getEmployeesSize(); i++) {
            ArrayList<Adventurer> employees = adventurer.getEmployees();
            if (!arrayList.contains(employees.get(i).getId())) {
                arrayList.add(employees.get(i).getId());
            }
            dfs(dep + 1,arrayList,employees.get(i));
        }
    }

    public static void hire(ArrayList<String> strings,
                            HashMap<Integer, Adventurer> adventurers) {
        int advId1 = Integer.parseInt(strings.get(1));
        int advId2 = Integer.parseInt(strings.get(2));
        adventurers.get(advId1).hire(adventurers.get(advId2));
        adventurers.get(advId2).beHired(adventurers.get(advId1));
    }

    public static void explore(ArrayList<String> strings,
                               HashMap<Integer, Adventurer> adventurers) {
        int advId = Integer.parseInt(strings.get(1));
        int totalCe = adventurers.get(advId).getTotalCe();
        if (totalCe > 1000) {
            System.out.println("Cloak of Shadows");
            adventurers.get(advId).addAllDef(40);
            totalCe = adventurers.get(advId).getTotalCe();
        }
        if (totalCe > 2000) {
            System.out.println("Flamebrand Sword");
            adventurers.get(advId).addAllAtk(40);
            totalCe = adventurers.get(advId).getTotalCe();
        }
        if (totalCe > 3000) {
            System.out.println("Stoneheart Amulet");
            adventurers.get(advId).addAllDef(40);
            totalCe = adventurers.get(advId).getTotalCe();
        }
        if (totalCe > 4000) {
            System.out.println("Windrunner Boots");
            adventurers.get(advId).addAllDef(30);
            totalCe = adventurers.get(advId).getTotalCe();
        }
        if (totalCe > 5000) {
            System.out.println("Frostbite Staff");
            adventurers.get(advId).addAllAtk(50);
        }
    }
}
