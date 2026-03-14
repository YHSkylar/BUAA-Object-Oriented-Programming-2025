public class DefBottle extends Bottle {
    public DefBottle(int id, String name, int capacity, int ce) {
        super(id,name,capacity, ce);
    }

    @Override
    public int useBottle(int def) {
        super.setEmpty();
        return def + super.getCe() + super.getCapacity() / 100;
    }
}
