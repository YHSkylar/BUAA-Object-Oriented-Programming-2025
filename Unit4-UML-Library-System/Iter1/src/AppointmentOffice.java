import com.oocourse.library1.LibraryBookIsbn;
import com.oocourse.library1.LibraryBookState;
import com.oocourse.library1.LibraryMoveInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AppointmentOffice {
    private HashMap<String, Book> userBooks;
    private HashMap<String, User> appointUsers;
    private BookShelf bookShelf;

    public AppointmentOffice(BookShelf bookShelf) {
        this.bookShelf = bookShelf;
        userBooks = new HashMap<>();
        appointUsers = new HashMap<>();
    }

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
                LibraryBookIsbn bookIsbn = book.getId().getBookIsbn();
                bookShelf.addBook(bookIsbn, book); //加入书架
                LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                    LibraryBookState.APPOINTMENT_OFFICE, LibraryBookState.BOOKSHELF);
                infos.add(info);
                book.beTidied(LibraryBookState.BOOKSHELF, date); //修改book状态，并添加history
                book.finishAppointed(); //book到达预约处日期清零
                user.finishAppoint(); //user取消预约
                iterator.remove(); //预约处中删除book
            }
        }
        return infos;
    }
}
