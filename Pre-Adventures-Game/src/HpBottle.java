public class HpBottle extends Bottle {
    public HpBottle(int id, String name, int capacity, int ce) {
        super(id,name,capacity, ce);
    }

    @Override
    public int useBottle(int hitPoint) {
        super.setEmpty();
        return hitPoint + super.getCapacity();
    }
}
