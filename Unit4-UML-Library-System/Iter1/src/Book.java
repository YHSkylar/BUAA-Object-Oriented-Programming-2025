import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryBookState;
import com.oocourse.library1.LibraryTrace;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Book {
    private LibraryBookId id;
    private LibraryBookState currentStatus;
    private ArrayList<LibraryTrace> history;
    private LocalDate arriveAppointmentOfficeDate;
    private String arriveTime;

    public Book(LibraryBookId id) {
        this.id = id;
        currentStatus = LibraryBookState.BOOKSHELF;
        history = new ArrayList<>();
        arriveAppointmentOfficeDate = null;
        arriveTime = null;
    }

    public LibraryBookId getId() {
        return id;
    }

    public void beBorrowed(LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus, LibraryBookState.USER);
        history.add(trace);
        currentStatus = LibraryBookState.USER;
    }

    public void beReturned(LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus,
            LibraryBookState.BORROW_RETURN_OFFICE);
        history.add(trace);
        currentStatus = LibraryBookState.BORROW_RETURN_OFFICE;
    }

    public void bePicked(LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus, LibraryBookState.USER);
        history.add(trace);
        currentStatus = LibraryBookState.USER;
    }

    public void beTidied(LibraryBookState to, LocalDate date) {
        LibraryTrace trace = new LibraryTrace(date, currentStatus, to);
        history.add(trace);
        currentStatus = to;
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
}
