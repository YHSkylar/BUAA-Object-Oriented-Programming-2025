public class Equipment extends Item {
    private int durability;

    public Equipment(int id, String name, int durability,int ce) {
        super(id,name, ce);
        this.durability = durability;
    }

    public int getDurability() {
        return durability;
    }

    public void add_dur() {
        durability += 1;
    }

    public int attack(int hitPoint, int atk, int def) {
        return hitPoint;
    }

    public void useEquipment() {
        durability -= 1;
    }

}
