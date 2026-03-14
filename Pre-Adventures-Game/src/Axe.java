public class Axe extends Equipment {
    public Axe(int id, String name, int durability, int ce) { super(id,name,durability,ce); }

    @Override
    public int attack(int hitPoint, int atk, int def) {
        return hitPoint / 10;
    }
}
