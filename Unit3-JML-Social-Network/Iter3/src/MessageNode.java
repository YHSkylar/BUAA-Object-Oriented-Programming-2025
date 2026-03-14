import com.oocourse.spec3.main.MessageInterface;

public class MessageNode {
    private Message message;
    private MessageNode prev;
    private MessageNode next;

    public MessageNode(MessageNode prev, MessageNode next) {
        this.prev = prev;
        this.next = next;
    }

    public MessageNode(Message message, MessageNode prev, MessageNode next) {
        this.message = message;
        this.prev = prev;
        this.next = next;
    }

    public MessageNode getNext() {
        return next;
    }

    public MessageNode getPrev() {
        return prev;
    }

    public void setNext(MessageNode next) {
        this.next = next;
    }

    public void setPrev(MessageNode prev) {
        this.prev = prev;
    }

    public MessageInterface getMessage() {
        return message;
    }
}
