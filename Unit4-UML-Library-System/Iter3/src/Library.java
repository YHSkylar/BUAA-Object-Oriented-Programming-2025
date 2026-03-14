import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryBookIsbn;
import com.oocourse.library3.LibraryBookState;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.LibraryQcsCmd;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryTrace;
import com.oocourse.library3.annotation.SendMessage;

import static com.oocourse.library3.LibraryIO.PRINTER;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
        editUsersCreditScore(this.date, date);
        this.date = date;
    }

    private void editUsersCreditScore(LocalDate lastDay, LocalDate today) {
        for (User user : users.values()) {
            ArrayList<Book> bookList = user.getBookLists();
            for (Book book : bookList) {
                //借书逾期
                if (book.isOverBorrowedLimit(today)) {
                    if (book.isOverBorrowedLimit(lastDay)) {
                        long gapDays = ChronoUnit.DAYS.between(lastDay, today);
                        user.minusCreditScore((int) (5 * gapDays));
                    } else {
                        LocalDate deadline;
                        if (book.getId().isTypeB()) {
                            deadline = book.getBorrowedDate().plusDays(30);
                        } else {
                            deadline = book.getBorrowedDate().plusDays(60);
                        }
                        long gapDays = ChronoUnit.DAYS.between(deadline, today);
                        user.minusCreditScore((int) (5 * gapDays));
                    }
                }
            }
        }

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

    public void queryCreditScore(LibraryQcsCmd qcs) {
        String userId = qcs.getStudentId();
        User user;
        if (!users.containsKey(userId)) {
            user = new User(userId);
            users.put(userId, user);
        } else {
            user = users.get(userId);
        }
        PRINTER.info(qcs, user.getCreditScore());
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

            if (user.getCreditScore() < 60) {
                PRINTER.reject(req);
                return;
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

    @SendMessage(from = "Library", to = "BookShelf")
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

            if (user.getCreditScore() < 100) {
                PRINTER.reject(req);
                return;
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
        if (!book.isOverBorrowedLimit(date)) {
            user.addCreditScore(10);
            PRINTER.accept(req, "not overdue");
        } else {
            PRINTER.accept(req, "overdue");
        }
        book.beReturned(date);
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

            int creditScore = user.getCreditScore();
            if (creditScore <= 0 || (creditScore < 40 && bookIsbn.isTypeA())) {
                PRINTER.reject(req);
                return;
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
        user.addCreditScore(10);
        PRINTER.accept(req);
    }
}
