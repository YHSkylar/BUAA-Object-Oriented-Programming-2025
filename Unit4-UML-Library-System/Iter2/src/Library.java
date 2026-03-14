import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryBookIsbn;
import com.oocourse.library2.LibraryBookState;
import com.oocourse.library2.LibraryMoveInfo;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryTrace;

import static com.oocourse.library2.LibraryIO.PRINTER;

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
    private final HotBookShelf hotBookShelf;
    private final ReadingRoom readingRoom;
    private final ArrayList<LibraryBookIsbn> hotBooks;

    public Library(Map<LibraryBookIsbn, Integer> bookLists) {
        bookShelf = new BookShelf(bookLists);
        books = bookShelf.recordBooks();
        hotBookShelf = new HotBookShelf(bookShelf);
        borrowReturnOffice = new BorrowReturnOffice(bookShelf, hotBookShelf);
        appointmentOffice = new AppointmentOffice(bookShelf, hotBookShelf);
        readingRoom = new ReadingRoom(bookShelf, hotBookShelf);
        date = null;
        users = new HashMap<>();
        waitAppointedBooks = new HashMap<>();
        hotBooks = new ArrayList<>();
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
        //3.阅览室书籍退回
        ArrayList<LibraryMoveInfo> info3 = readingRoom.returnAllBook(date, users);
        moveInfos.addAll(info3);
        //4.预约书籍前往预约处
        Iterator<LibraryBookIsbn> appointedBooksIterator = waitAppointedBooks.keySet().iterator();
        while (appointedBooksIterator.hasNext()) {
            LibraryBookIsbn bookIsbn = appointedBooksIterator.next();
            if (bookShelf.containsBook(bookIsbn) | hotBookShelf.containsBook(bookIsbn)) {
                Book book;
                LibraryBookState currentStatus;
                if (bookShelf.containsBookIsbn(bookIsbn)) {
                    book = bookShelf.getBook(bookIsbn);
                    currentStatus = LibraryBookState.BOOKSHELF;

                } else {
                    book = hotBookShelf.getBook(bookIsbn);
                    currentStatus = LibraryBookState.HOT_BOOKSHELF;
                }
                String userId = waitAppointedBooks.get(bookIsbn);
                User user = users.get(userId);
                appointmentOffice.addUserBook(userId, book, user);
                book.beTidied(LibraryBookState.APPOINTMENT_OFFICE, date);
                book.arriveAppointmentOffice(date, time);
                LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                    currentStatus, LibraryBookState.APPOINTMENT_OFFICE, userId);
                moveInfos.add(info);
                appointedBooksIterator.remove();
            }
        }

        if (time.equals("OPEN")) {
            //5.热门书架退回冷门书
            ArrayList<LibraryMoveInfo> info4 = hotBookShelf.returnColdBooks(date, hotBooks);
            moveInfos.addAll(info4);
            //6.普通书架 to 热门书架
            Iterator<LibraryBookIsbn> hotBooksIterator = hotBooks.iterator();
            while (hotBooksIterator.hasNext()) {
                LibraryBookIsbn bookIsbn = hotBooksIterator.next();
                if (bookShelf.containsBookIsbn(bookIsbn)) {
                    ArrayList<Book> normal2hot = bookShelf.removeBookList(bookIsbn);
                    hotBookShelf.addBookList(bookIsbn, normal2hot);
                    for (Book book : normal2hot) {
                        book.beTidied(LibraryBookState.HOT_BOOKSHELF, date);
                        LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                            LibraryBookState.BOOKSHELF, LibraryBookState.HOT_BOOKSHELF);
                        moveInfos.add(info);
                    }
                }
                hotBooksIterator.remove();
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
        if (!(bookShelf.containsBook(bookIsbn) | hotBookShelf.containsBook(bookIsbn)) ||
            bookIsbn.isTypeA()) {
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
                Book book;
                if (bookShelf.containsBook(bookIsbn)) {
                    book = bookShelf.getBook(bookIsbn);
                } else {
                    book = hotBookShelf.getBook(bookIsbn);
                }
                user.receiveBook(bookIsbn, book);
                book.beBorrowed(date);
                //记录热门书籍
                hotBooks.add(bookIsbn);
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

    public void read(LibraryReqCmd req) {
        LibraryBookIsbn bookIsbn = req.getBookIsbn();
        String userId = req.getStudentId();

        if (!(bookShelf.containsBook(bookIsbn) | hotBookShelf.containsBook(bookIsbn))) {
            PRINTER.reject(req);
        } else {
            User user;
            if (!users.containsKey(userId)) {
                user = new User(userId);
                users.put(userId, user);
            } else {
                user = users.get(userId);
            }

            if (user.isReading()) {
                PRINTER.reject(req);
            } else {
                Book book;
                if (bookShelf.containsBook(bookIsbn)) {
                    book = bookShelf.getBook(bookIsbn);
                } else {
                    book = hotBookShelf.getBook(bookIsbn);
                }
                readingRoom.receiveBook(userId, book);
                book.beRead(date);
                user.read();
                //记录热门书籍
                hotBooks.add(bookIsbn);
                PRINTER.accept(req, book.getId());
            }
        }
    }

    public void restored(LibraryReqCmd req) {
        String userId = req.getStudentId();

        Book book = readingRoom.returnBook(userId);
        User user = users.get(userId);
        user.finishRead();
        borrowReturnOffice.receiveBook(book);
        book.beReturned(date);
        PRINTER.accept(req);
    }
}
