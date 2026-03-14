import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Adventurer {
    private int id;
    private String name;
    private int hitPoint;
    private int atk;
    private int def;
    private int ce;
    private HashMap<Integer, Item> wirehouse;
    private HashMap<Integer, Item> backpack;
    private HashMap<String, ArrayList<Fragment>> fragments;
    private HashMap<Integer,Adventurer> employees;
    private HashMap<Integer,Integer> helpTimes;
    private HashMap<Integer,Integer> employersHitpoint;

    public Adventurer(int id, String name) {
        this.id = id;
        this.name = name;
        this.hitPoint = 500;
        this.atk = 1;
        this.def = 0;
        this.ce = 1;
        this.wirehouse = new HashMap<>();
        this.backpack = new HashMap<>();
        this.fragments = new HashMap<>();
        this.employees = new HashMap<>();
        this.helpTimes = new HashMap<>();
        this.employersHitpoint = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHitPoint() {
        return hitPoint;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getCE() {
        return ce;
    }

    public void add_item(int id, Item item) {
        wirehouse.put(id,item);
    }

    public void add_equipmentDurability_inW(int id) {
        if (wirehouse.get(id) instanceof Equipment) {
            ((Equipment) wirehouse.get(id)).add_dur();
        }
    }

    public Item findItem_inWirehouse(int id) {
        return wirehouse.get(id);
    }

    public void del_item_inW(int id) {
        wirehouse.remove(id);
    }

    public void del_item_inB(int id) {
        backpack.remove(id);
    }

    public void take_item(int id) {
        Item value = wirehouse.get(id);
        String name = value.getName();
        if (value instanceof Equipment) {
            int flag = 0;
            for (Item temp : backpack.values()) {
                if (temp instanceof Equipment && temp.getName().equals(name)) {
                    backpack.remove(temp.getId());
                    backpack.put(id,value);
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                backpack.put(id,value);
            }
        }
        else if (value instanceof Bottle) {
            int maxBottles = this.ce / 5 + 1;
            int count = 0;
            for (Item temp : backpack.values()) {
                if (temp instanceof Bottle && temp.getName().equals(name)) {
                    count++;
                }
            }
            if (count < maxBottles) {
                backpack.put(id,value);
            }
        }
    }

    public Boolean ifContain_inB(int id) {
        if (backpack.containsKey(id)) {
            return true;
        }
        else {
            return false;
        }
    }

    public Item findItem_inBackpack(int id) {
        return backpack.get(id);
    }

    public void useBottle(int id) {
        if (backpack.get(id) instanceof HpBottle) {
            this.hitPoint = ((HpBottle) backpack.get(id)).useBottle(this.hitPoint);
            for (Adventurer adventurer : employees.values()) {
                adventurer.employersHitpoint.put(this.id,this.hitPoint);
            }
        }
        else if (backpack.get(id) instanceof AtkBottle) {
            this.atk = ((AtkBottle) backpack.get(id)).useBottle(this.atk);
        }
        else if (backpack.get(id) instanceof DefBottle) {
            this.def = ((DefBottle) backpack.get(id)).useBottle(this.def);
        }
        this.ce = this.atk + this.def;
    }

    public ArrayList<Fragment> getFragments(String name) {
        return fragments.get(name);
    }

    public void addFragment(String name, Fragment fragment) {
        if (fragments.containsKey(name)) {
            fragments.get(name).add(fragment);
        }
        else {
            ArrayList<Fragment> fragmentName = new ArrayList<>();
            fragmentName.add(fragment);
            fragments.put(name,fragmentName);
        }
    }

    public Boolean whetherUseFragment(String name) {
        if (fragments.get(name).size() >= 5) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getFragmentNameSize(String name) {
        return fragments.get(name).size();
    }

    public char useFragment(String name, int welfareId) {
        for (int i = 0; i < 5; i++) {
            fragments.get(name).remove(0);
        }

        if (wirehouse.containsKey(welfareId)) { //a & b
            Item item = wirehouse.get(welfareId);
            if (item instanceof Bottle) {
                if (((Bottle) item).getIfEmpty()) {
                    ((Bottle) item).setFull();
                }
                return 'a';
            }
            else if (item instanceof Equipment) {
                ((Equipment) item).add_dur();
                return 'b';
            }
        }
        else { //c
            Item item = new HpBottle(welfareId,name,100,0);
            wirehouse.put(welfareId,item);
            return 'c';
        }
        return 0;
    }

    public Item findEquipmentInBackpack(String name) {
        for (Item item : backpack.values()) {
            if (item instanceof Equipment && item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public void beAttacked(Item item,int atk) {
        if (item instanceof Axe) {
            this.hitPoint = ((Axe) item).attack(hitPoint,atk,def);
        }
        else if (item instanceof Sword) {
            this.hitPoint = ((Sword) item).attack(hitPoint,atk,def);
        }
        else if (item instanceof Blade) {
            this.hitPoint = ((Blade) item).attack(hitPoint,atk,def);
        }
    }

    public void useEquipment(Item item) {
        if (item instanceof Equipment) {
            ((Equipment) item).useEquipment();
            if (((Equipment) item).getDurability() == 0) {
                backpack.remove(item.getId());
                wirehouse.remove(item.getId());
            }
        }
    }

    public void hire(Adventurer employee) {
        int id = employee.getId();
        employees.put(id, employee);
    }

    public void beHired(Adventurer employer) {
        int id = employer.getId();
        int hitPoint = employer.getHitPoint();
        helpTimes.put(id,0);
        employersHitpoint.put(id,hitPoint);
    }

    public void help(Adventurer employer) {
        int id = employer.getId();
        int hitPointBefore = employersHitpoint.get(id);
        int hitPointAfter = employer.getHitPoint();
        employersHitpoint.put(id,hitPointAfter);
        if (hitPointAfter <= hitPointBefore / 2) {
            Iterator<Map.Entry<Integer,Item>> iterator = backpack.entrySet().iterator();
            Map.Entry<Integer,Item> temp;
            while (iterator.hasNext()) {
                temp = iterator.next();
                Integer key = temp.getKey();
                Item item = temp.getValue();
                if (item instanceof Equipment) {
                    employer.add_item(key,item);
                    wirehouse.remove(key);
                    iterator.remove();
                }
            }  // 迭代器删除
            helpTimes.put(id,helpTimes.get(id) + 1);
            if (helpTimes.get(id) > 3) {
                helpTimes.remove(id);
                employersHitpoint.remove(id);
                employer.removeEmployee(this.id);
            }
        }
    }

    public void removeEmployee(int id) {
        employees.remove(id);
    }

    public int getEmployeesSize() {
        return employees.size();
    }

    public ArrayList<Adventurer> getEmployees() {
        ArrayList<Adventurer> employees = new ArrayList<>();
        employees.addAll(this.employees.values()); //浅拷贝
        return employees;
    }

    public int getTotalCe() {
        int ans = ce;
        for (Item item : backpack.values()) {
            ans = ans + item.getCe();
        }
        for (Adventurer adventurer : employees.values()) {
            ans = ans + adventurer.getCE();
        }
        return ans;
    }

    public void addAllDef(int m) {
        this.addDef(m);
        for (Adventurer adventurer : employees.values()) {
            adventurer.addDef(m);
        }
    }

    public void addDef(int m) {
        def = def + m;
        ce = atk + def;
    }

    public void addAllAtk(int m) {
        this.addAtk(m);
        for (Adventurer adventurer : employees.values()) {
            adventurer.addAtk(m);
        }
    }

    public void addAtk(int m) {
        atk = atk + m;
        ce = atk + def;
    }
}
