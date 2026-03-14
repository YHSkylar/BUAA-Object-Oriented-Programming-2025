import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryBookIsbn;
import com.oocourse.library1.LibraryBookState;
import com.oocourse.library1.LibraryMoveInfo;
import com.oocourse.library1.LibraryReqCmd;
import com.oocourse.library1.LibraryTrace;

import static com.oocourse.library1.LibraryIO.PRINTER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Library {
    private final BookShelf bookShelf;
    private final HashMap<LibraryBookId, Book> books;
    private final BorrowReturnOffice borrowReturnOffice;
    private final AppointmentOffice appointmentOffice;
    private LocalDate date;
    private final HashMap<String, User> users;
    private final HashMap<LibraryBookIsbn, String> waitAppointedBooks;

    public Library(Map<LibraryBookIsbn, Integer> bookLists) {
        bookShelf = new BookShelf(bookLists);
        books = bookShelf.recordBooks();
        borrowReturnOffice = new BorrowReturnOffice(bookShelf);
        appointmentOffice = new AppointmentOffice(bookShelf);
        date = null;
        users = new HashMap<>();
        waitAppointedBooks = new HashMap<>();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void tidy(String time) {
        //1.退回借还处书籍
        ArrayList<LibraryMoveInfo> info1 = borrowReturnOffice.returnAllBooks(date);
        ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>(info1);
        //2.预约处逾期书籍退回
        ArrayList<LibraryMoveInfo> info2 = appointmentOffice.returnOverdueBook(date, time);
        moveInfos.addAll(info2);
        //3.预约书籍前往预约处
        Iterator<LibraryBookIsbn> iterator = waitAppointedBooks.keySet().iterator();
        while (iterator.hasNext()) {
            LibraryBookIsbn bookIsbn = iterator.next();
            if (bookShelf.containsBook(bookIsbn)) {
                Book book = bookShelf.getBook(bookIsbn);
                String userId = waitAppointedBooks.get(bookIsbn);
                User user = users.get(userId);
                appointmentOffice.addUserBook(userId, book, user);
                book.beTidied(LibraryBookState.APPOINTMENT_OFFICE, date);
                book.arriveAppointmentOffice(date, time);
                LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                    LibraryBookState.BOOKSHELF, LibraryBookState.APPOINTMENT_OFFICE, userId);
                moveInfos.add(info);
                iterator.remove();
            }
        }

        PRINTER.move(date, moveInfos);
    }

    public void queried(LibraryReqCmd req) {
        LibraryBookId bookId = req.getBookId();

        Book book = books.get(bookId);
        ArrayList<LibraryTrace> bookTraces = book.getBookTrace();
        PRINTER.info(date, bookId, bookTraces);
    }

    public void borrowed(LibraryReqCmd req) {
        LibraryBookIsbn bookIsbn = req.getBookIsbn();
        String userId = req.getStudentId();
        if (!bookShelf.containsBook(bookIsbn) || bookIsbn.isTypeA()) {
            //借阅失败
            PRINTER.reject(req);
        } else {
            User user;
            if (!users.containsKey(userId)) {
                user = new User(userId);
                users.put(userId, user);
            } else {
                user = users.get(userId);
            }

            if ((user.hasBookTypeB() && bookIsbn.isTypeB()) || user.hasBook(bookIsbn)) {
                //借阅失败
                PRINTER.reject(req);
            } else {
                //借阅成功
                Book book = bookShelf.getBook(bookIsbn);
                user.receiveBook(bookIsbn, book);
                book.beBorrowed(date);
                PRINTER.accept(req, book.getId());
            }
        }
    }

    public void ordered(LibraryReqCmd req) {
        LibraryBookIsbn bookIsbn = req.getBookIsbn();
        String userId = req.getStudentId();

        if (bookIsbn.isTypeA()) {
            PRINTER.reject(req);
        } else {
            User user;
            if (!users.containsKey(userId)) {
                user = new User(userId);
                users.put(userId, user);
            } else {
                user = users.get(userId);
            }

            if ((user.hasBookTypeB() && bookIsbn.isTypeB()) ||
                user.hasBook(bookIsbn) || user.isAppointing()) {
                PRINTER.reject(req);
            } else {
                waitAppointedBooks.put(bookIsbn, userId);
                user.doAppoint();
                PRINTER.accept(req);
            }
        }
    }

    public void returned(LibraryReqCmd req) {
        LibraryBookId bookId = req.getBookId();
        String userId = req.getStudentId();

        User user = users.get(userId);
        Book book = user.returnBook(bookId);
        borrowReturnOffice.receiveBook(book);
        book.beReturned(date);
        PRINTER.accept(req);
    }

    public void picked(LibraryReqCmd req) {
        LibraryBookIsbn bookIsbn = req.getBookIsbn();
        String userId = req.getStudentId();

        User user = users.get(userId);
        if (appointmentOffice.containsUserBook(userId, bookIsbn)) {
            if ((user.hasBookTypeB() && bookIsbn.isTypeB()) || user.hasBook(bookIsbn)) {
                PRINTER.reject(req);
            } else {
                Book book = appointmentOffice.getUserBook(userId);
                user.receiveBook(bookIsbn, book);
                user.finishAppoint();
                book.bePicked(date);
                PRINTER.accept(req, book.getId());
            }
        } else {
            PRINTER.reject(req);
        }
    }
}
