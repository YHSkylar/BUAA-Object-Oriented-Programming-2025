public class AtkBottle extends Bottle {
    public AtkBottle(int id, String name, int capacity, int ce) {
        super(id,name,capacity, ce);
    }

    @Override
    public int useBottle(int atk) {
        super.setEmpty();
        return atk + super.getCe() + super.getCapacity() / 100;
    }
}
