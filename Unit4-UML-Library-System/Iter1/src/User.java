import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryBookIsbn;

import java.util.HashMap;

public class User {
    private String id;
    private HashMap<LibraryBookIsbn, Book> books;
    private boolean hasBookTypeB;
    private boolean isAppointing;

    public User(String userId) {
        this.id = userId;
        books = new HashMap<>();
        hasBookTypeB = false;
        isAppointing = false;
    }

    public boolean hasBookTypeB() {
        return hasBookTypeB;
    }

    public boolean hasBook(LibraryBookIsbn bookIsbn) {
        return books.containsKey(bookIsbn);
    }

    public void receiveBook(LibraryBookIsbn bookIsbn, Book book) {
        books.put(bookIsbn, book);
        if (bookIsbn.isTypeB()) {
            hasBookTypeB = true;
        }
    }

    public Book returnBook(LibraryBookId bookId) {
        LibraryBookIsbn bookIsbn = bookId.getBookIsbn();
        Book book = books.get(bookIsbn);
        books.remove(bookIsbn);
        if (bookIsbn.isTypeB()) {
            hasBookTypeB = false;
        }
        return book;
    }

    public boolean isAppointing() {
        return isAppointing;
    }

    public void doAppoint() {
        isAppointing = true;
    }

    public void finishAppoint() {
        isAppointing = false;
    }
}
