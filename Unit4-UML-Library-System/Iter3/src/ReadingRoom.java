import com.oocourse.library3.LibraryBookIsbn;
import com.oocourse.library3.LibraryBookState;
import com.oocourse.library3.LibraryMoveInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReadingRoom {
    private HashMap<String, Book> books;
    private BookShelf bookShelf;
    private HotBookShelf hotBookShelf;

    public ReadingRoom(BookShelf bookShelf, HotBookShelf hotBookShelf) {
        books = new HashMap<>();
        this.bookShelf = bookShelf;
        this.hotBookShelf = hotBookShelf;
    }

    public void receiveBook(String userId, Book book) {
        books.put(userId, book);
    }

    public Book returnBook(String userId) {
        Book book = books.get(userId);
        books.remove(userId);
        return book;
    }

    public ArrayList<LibraryMoveInfo> returnAllBook(LocalDate date, HashMap<String, User> users) {
        ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();
        Iterator<Map.Entry<String, Book>> iterator = books.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Book> entry = iterator.next();
            Book book = entry.getValue();
            LibraryBookIsbn bookIsbn = book.getId().getBookIsbn();
            if (bookShelf.containsBookIsbn(bookIsbn)) {
                bookShelf.addBook(bookIsbn, book);
                LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                    LibraryBookState.READING_ROOM, LibraryBookState.BOOKSHELF);
                moveInfos.add(info);
                book.beTidied(LibraryBookState.BOOKSHELF, date);
            } else {
                hotBookShelf.addBook(bookIsbn, book);
                LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                    LibraryBookState.READING_ROOM, LibraryBookState.HOT_BOOKSHELF);
                moveInfos.add(info);
                book.beTidied(LibraryBookState.HOT_BOOKSHELF, date);
            }
            String userId = entry.getKey();
            User user = users.get(userId);
            user.finishRead();
            user.minusCreditScore(10);
            iterator.remove();
        }
        return moveInfos;
    }
}
