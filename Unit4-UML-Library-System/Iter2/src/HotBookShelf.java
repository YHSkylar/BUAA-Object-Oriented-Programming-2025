import com.oocourse.library2.LibraryBookIsbn;
import com.oocourse.library2.LibraryBookState;
import com.oocourse.library2.LibraryMoveInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HotBookShelf {
    private HashMap<LibraryBookIsbn, ArrayList<Book>> books;
    private BookShelf bookShelf;

    public HotBookShelf(BookShelf bookShelf) {
        books = new HashMap<>();
        this.bookShelf = bookShelf;
    }

    public boolean containsBook(LibraryBookIsbn bookIsbn) {
        if (books.containsKey(bookIsbn)) {
            return !books.get(bookIsbn).isEmpty();
        } else {
            return false;
        }
    }

    public void addBook(LibraryBookIsbn bookIsbn, Book book) {
        books.get(bookIsbn).add(book);
    }

    public Book getBook(LibraryBookIsbn bookIsbn) {
        Book book = books.get(bookIsbn).get(0);
        books.get(bookIsbn).remove(0);
        return book;
    }

    public void addBookList(LibraryBookIsbn bookIsbn, ArrayList<Book> books) {
        this.books.put(bookIsbn, books);
    }

    public ArrayList<LibraryMoveInfo> returnColdBooks(LocalDate date,
        ArrayList<LibraryBookIsbn> hotBookList) {
        ArrayList<LibraryMoveInfo> moveInfos = new ArrayList<>();
        Iterator<Map.Entry<LibraryBookIsbn, ArrayList<Book>>> iterator =
            books.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LibraryBookIsbn, ArrayList<Book>> entry = iterator.next();
            LibraryBookIsbn bookIsbn = entry.getKey();
            if (!hotBookList.contains(bookIsbn)) {
                ArrayList<Book> bookList = entry.getValue();
                bookShelf.addBookList(bookIsbn, bookList);
                for (Book book : bookList) {
                    book.beTidied(LibraryBookState.BOOKSHELF, date);
                    LibraryMoveInfo info = new LibraryMoveInfo(book.getId(),
                        LibraryBookState.HOT_BOOKSHELF, LibraryBookState.BOOKSHELF);
                    moveInfos.add(info);
                }
                iterator.remove();
            }
        }
        return moveInfos;
    }
}
