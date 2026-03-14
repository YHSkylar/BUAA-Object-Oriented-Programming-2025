import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryBookState;
import com.oocourse.library3.LibraryTrace;
import com.oocourse.library3.annotation.Trigger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Book {
    private LibraryBookId id;
    private LibraryBookState currentStatus;
    private ArrayList<LibraryTrace> history;
    private LocalDate arriveAppointmentOfficeDate;
    private String arriveTime;
    private LocalDate borrowedDate;

    public Book(LibraryBookId id) {
        this.id = id;
        currentStatus = LibraryBookState.BOOKSHELF;
        history = new ArrayList<>();
        arriveAppointmentOfficeDate = null;
        arriveTime = null;
        borrowedDate = null;
    }

    public LibraryBookId getId() {
        return id;
    }

    @Trigger(from = "BookShelf", to = "User")
    @Trigger(from = "HotBookShelf", to = "User")
    public void beBorrowed(LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus, LibraryBookState.USER);
        history.add(trace);
        currentStatus = LibraryBookState.USER;
        borrowedDate = date;
    }

    @Trigger(from = "User", to = "BorrowReturnOffice")
    @Trigger(from = "ReadingRoom", to = "BorrowReturnOffice")
    public void beReturned(LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus,
            LibraryBookState.BORROW_RETURN_OFFICE);
        history.add(trace);
        currentStatus = LibraryBookState.BORROW_RETURN_OFFICE;
        borrowedDate = null;
    }

    @Trigger(from = "AppointmentOffice", to = "User")
    public void bePicked(LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus, LibraryBookState.USER);
        history.add(trace);
        currentStatus = LibraryBookState.USER;
        borrowedDate = date;
    }

    @Trigger(from = "BorrowReturnOffice", to = {"BookShelf", "HotBookShelf"})
    @Trigger(from = "HotBookShelf", to = {"BookShelf", "AppointmentOffice"})
    @Trigger(from = "ReadingRoom", to = {"BookShelf", "HotBookShelf"})
    @Trigger(from = "AppointmentOffice", to = {"BookShelf", "HotBookShelf"})
    @Trigger(from = "BookShelf", to = {"HotBookShelf", "AppointmentOffice"})
    public void beTidied(LibraryBookState to, LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus, to);
        history.add(trace);
        currentStatus = to;
    }

    @Trigger(from = "BookShelf", to = "ReadingRoom")
    @Trigger(from = "HotBookShelf", to = "ReadingRoom")
    public void beRead(LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus, LibraryBookState.READING_ROOM);
        history.add(trace);
        currentStatus = LibraryBookState.READING_ROOM;
    }

    public ArrayList<LibraryTrace> getBookTrace() {
        return history;
    }

    public void arriveAppointmentOffice(LocalDate date, String arriveTime) {
        arriveAppointmentOfficeDate = date;
        this.arriveTime = arriveTime;
    }

    public boolean isOverDue(LocalDate today, String time) {
        long daysBetween = ChronoUnit.DAYS.between(arriveAppointmentOfficeDate, today);
        if (arriveTime.equals("OPEN")) {
            return (daysBetween >= 4 && time.equals("CLOSE")) ||
                (daysBetween >= 5 && time.equals("OPEN"));
        } else {
            return (daysBetween >= 5 && time.equals("CLOSE")) ||
                (daysBetween >= 6 && time.equals("OPEN"));
        }

    }

    public void finishAppointed() {
        arriveAppointmentOfficeDate = null;
        arriveTime = null;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public boolean isOverBorrowedLimit(LocalDate today) {
        if (!(currentStatus == LibraryBookState.USER)) {
            return false;
        } else {
            long gapDays = ChronoUnit.DAYS.between(borrowedDate, today);
            if (id.isTypeB()) {
                return gapDays > 30;
            } else {
                return gapDays > 60;
            }
        }
    }
}
