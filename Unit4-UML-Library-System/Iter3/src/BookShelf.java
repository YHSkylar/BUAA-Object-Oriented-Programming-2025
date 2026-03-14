import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryBookIsbn;
import com.oocourse.library3.annotation.SendMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookShelf {
    private HashMap<LibraryBookIsbn, ArrayList<Book>> books;

    public BookShelf(Map<LibraryBookIsbn, Integer> bookLists) {
        books = new HashMap<>();
        for (LibraryBookIsbn isbn : bookLists.keySet()) {
            ArrayList<Book> array = new ArrayList<>();
            books.put(isbn, array);
            int num = bookLists.get(isbn);
            for (int i = 0; i < num; i++) {
                LibraryBookId bookId =
                    new LibraryBookId(isbn.getType(), isbn.getUid(), String.format("%02d", i + 1));
                Book book = new Book(bookId);
                array.add(book);
            }
        }
    }

    public HashMap<LibraryBookId, Book> recordBooks() {
        HashMap<LibraryBookId, Book> recordBooksList = new HashMap<>();
        for (LibraryBookIsbn isbn : books.keySet()) {
            ArrayList<Book> isbnBooks = books.get(isbn);
            for (Book book : isbnBooks) {
                recordBooksList.put(book.getId(), book);
            }
        }
        return recordBooksList;
    }

    public boolean containsBook(LibraryBookIsbn bookIsbn) {
        if (books.containsKey(bookIsbn)) {
            return !books.get(bookIsbn).isEmpty();
        } else {
            return false;
        }
    }

    @SendMessage(from = "BookShelf", to = "Library")
    public Book getBook(LibraryBookIsbn bookIsbn) {
        Book book = books.get(bookIsbn).get(0);
        books.get(bookIsbn).remove(0);
        return book;
    }

    public void addBook(LibraryBookIsbn bookIsbn, Book book) {
        books.get(bookIsbn).add(book);
    }

    public void addBookList(LibraryBookIsbn bookIsbn, ArrayList<Book> books) {
        this.books.put(bookIsbn, books);
    }

    public ArrayList<Book> removeBookList(LibraryBookIsbn bookIsbn) {
        ArrayList<Book> bookList = this.books.get(bookIsbn);
        this.books.remove(bookIsbn);
        return bookList;
    }

    public boolean containsBookIsbn(LibraryBookIsbn bookIsbn) {
        return books.containsKey(bookIsbn);
    }
}
