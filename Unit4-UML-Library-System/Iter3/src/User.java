import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryBookIsbn;
import com.oocourse.library3.annotation.SendMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String id;
    private HashMap<LibraryBookIsbn, Book> books;
    private boolean hasBookTypeB;
    private boolean isAppointing;
    private boolean isReading;
    private int creditScore;

    public User(String userId) {
        this.id = userId;
        books = new HashMap<>();
        hasBookTypeB = false;
        isAppointing = false;
        isReading = false;
        creditScore = 100;
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

    public boolean isReading() {
        return isReading;
    }

    public void read() {
        isReading = true;
    }

    public void finishRead() {
        isReading = false;
    }

    public void addCreditScore(int x) {
        if (creditScore + x > 180) {
            creditScore = 180;
        } else {
            creditScore = creditScore + x;
        }
    }

    public void minusCreditScore(int x) {
        if (creditScore - x < 0) {
            creditScore = 0;
        } else {
            creditScore = creditScore - x;
        }
    }

    public int getCreditScore() {
        return creditScore;
    }

    public ArrayList<Book> getBookLists() {
        return new ArrayList<>(books.values());
    }

    @SendMessage(from = "User", to = "Library")
    public void orderNewBook() {
        return;
    }

    @SendMessage(from = "AppointmentOffice", to = "User")
    public void getOrderedBook() {
        return;
    }
}
