import com.oocourse.library1.LibraryBookIsbn;
import com.oocourse.library1.LibraryBookState;
import com.oocourse.library1.LibraryMoveInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class BorrowReturnOffice {
    private ArrayList<Book> books;
    private BookShelf bookShelf;

    public BorrowReturnOffice(BookShelf bookShelf) {
        this.bookShelf = bookShelf;
        books = new ArrayList<>();
    }

    public void receiveBook(Book book) {
        books.add(book);
    }

    public ArrayList<LibraryMoveInfo> returnAllBooks(LocalDate date) {
        ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();
        Iterator<Book> iterator = books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            LibraryBookIsbn bookIsbn = book.getId().getBookIsbn();
            bookShelf.addBook(bookIsbn, book);
            LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                LibraryBookState.BORROW_RETURN_OFFICE, LibraryBookState.BOOKSHELF);
            moveInfos.add(info);
            book.beTidied(LibraryBookState.BOOKSHELF, date);
            iterator.remove();
        }
        return moveInfos;
    }
}
