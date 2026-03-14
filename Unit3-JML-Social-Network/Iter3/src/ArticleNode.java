public class ArticleNode {
    private int id;
    private ArticleNode prev;
    private ArticleNode next;

    public ArticleNode(ArticleNode prev, ArticleNode next) {
        this.prev = prev;
        this.next = next;
    }

    public ArticleNode(int id, ArticleNode prev, ArticleNode next) {
        this.id = id;
        this.prev = prev;
        this.next = next;
    }

    public ArticleNode getNext() {
        return next;
    }

    public ArticleNode getPrev() {
        return prev;
    }

    public void setNext(ArticleNode next) {
        this.next = next;
    }

    public void setPrev(ArticleNode prev) {
        this.prev = prev;
    }

    public int getId() {
        return id;
    }
}
