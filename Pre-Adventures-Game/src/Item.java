public class Item {
    private int id;
    private String name;
    private int ce;

    public Item(int id, String name, int ce) {
        this.id = id;
        this.name = name;
        this.ce = ce;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCe() {
        return ce;
    }
}
