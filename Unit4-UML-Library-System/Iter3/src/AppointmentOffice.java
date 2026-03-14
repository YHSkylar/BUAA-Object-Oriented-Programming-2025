import com.oocourse.library3.LibraryBookIsbn;
import com.oocourse.library3.LibraryBookState;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.annotation.SendMessage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AppointmentOffice {
    private HashMap<String, Book> userBooks;
    private HashMap<String, User> appointUsers;
    private BookShelf bookShelf;
    private HotBookShelf hotBookShelf;

    public AppointmentOffice(BookShelf bookShelf, HotBookShelf hotBookShelf) {
        this.bookShelf = bookShelf;
        this.hotBookShelf = hotBookShelf;
        userBooks = new HashMap<>();
        appointUsers = new HashMap<>();
    }

    @SendMessage(from = "Library", to = "AppointmentOffice")
    public void addUserBook(String userId, Book book, User user) {
        userBooks.put(userId, book);
        appointUsers.put(userId, user);
    }

    public boolean containsUserBook(String userId, LibraryBookIsbn bookIsbn) {
        if (!userBooks.containsKey(userId)) {
            return false;
        } else {
            Book book = userBooks.get(userId);
            return book.getId().getBookIsbn().equals(bookIsbn);
        }
    }

    public Book getUserBook(String userId) {
        Book book = userBooks.get(userId);
        userBooks.remove(userId);
        return book;
    }

    public ArrayList<LibraryMoveInfo> returnOverdueBook(LocalDate date, String time) {
        ArrayList<LibraryMoveInfo> infos = new ArrayList<>();

        Iterator<Map.Entry<String, Book>> iterator = userBooks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Book> entry = iterator.next();
            String userId = entry.getKey();
            User user = appointUsers.get(userId);
            Book book = entry.getValue();
            if (book.isOverDue(date, time)) {
                user.minusCreditScore(15);
                LibraryBookIsbn bookIsbn = book.getId().getBookIsbn();
                if (bookShelf.containsBookIsbn(bookIsbn)) {
                    bookShelf.addBook(bookIsbn, book);
                    LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                        LibraryBookState.APPOINTMENT_OFFICE, LibraryBookState.BOOKSHELF);
                    infos.add(info);
                    book.beTidied(LibraryBookState.BOOKSHELF, date);
                } else {
                    hotBookShelf.addBook(bookIsbn, book);
                    LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                        LibraryBookState.APPOINTMENT_OFFICE, LibraryBookState.HOT_BOOKSHELF);
                    infos.add(info);
                    book.beTidied(LibraryBookState.HOT_BOOKSHELF, date);
                }
                book.finishAppointed(); //book到达预约处日期清零
                user.finishAppoint(); //user取消预约
                iterator.remove(); //预约处中删除book
            }
        }
        return infos;
    }
}
