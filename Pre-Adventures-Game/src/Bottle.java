public class Bottle extends Item {
    private int capacity;
    private Boolean ifEmpty;

    public Bottle(int id, String name, int capacity, int ce) {
        super(id,name, ce);
        this.capacity = capacity;
        this.ifEmpty = false;
    }

    public int getCapacity() {
        return capacity;
    }

    public int useBottle(int a) {
        this.ifEmpty = true;
        return a;
    }

    public Boolean getIfEmpty() {
        return ifEmpty;
    }

    public void setEmpty() {
        this.ifEmpty = true;
    }

    public void setFull() { this.ifEmpty = false; }
}
