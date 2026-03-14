import com.oocourse.library3.LibraryBookIsbn;
import com.oocourse.library3.LibraryBookState;
import com.oocourse.library3.LibraryMoveInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class BorrowReturnOffice {
    private ArrayList<Book> books;
    private BookShelf bookShelf;
    private HotBookShelf hotBookShelf;

    public BorrowReturnOffice(BookShelf bookShelf, HotBookShelf hotBookShelf) {
        this.bookShelf = bookShelf;
        this.hotBookShelf = hotBookShelf;
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
            if (bookShelf.containsBookIsbn(bookIsbn)) {
                bookShelf.addBook(bookIsbn, book);
                LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                    LibraryBookState.BORROW_RETURN_OFFICE, LibraryBookState.BOOKSHELF);
                moveInfos.add(info);
                book.beTidied(LibraryBookState.BOOKSHELF, date);
            } else {
                hotBookShelf.addBook(bookIsbn, book);
                LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                    LibraryBookState.BORROW_RETURN_OFFICE, LibraryBookState.HOT_BOOKSHELF);
                moveInfos.add(info);
                book.beTidied(LibraryBookState.HOT_BOOKSHELF, date);
            }
            iterator.remove();
        }
        return moveInfos;
    }
}
