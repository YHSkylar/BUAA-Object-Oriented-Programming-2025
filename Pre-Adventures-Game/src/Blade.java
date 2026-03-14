public class Blade extends Equipment {
    public Blade(int id, String name, int durability, int ce) { super(id,name,durability,ce); }

    @Override
    public int attack(int hitPoint, int atk, int def) {
        int hitPointDecrease = super.getCe() + atk;
        return hitPoint - hitPointDecrease;
    }
}
