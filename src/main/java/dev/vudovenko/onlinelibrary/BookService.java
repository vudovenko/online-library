package dev.vudovenko.onlinelibrary;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    private Long idCounter;
    private final Map<Long, Book> bookMap;

    public BookService() {
        this.idCounter = 0L;
        this.bookMap = new HashMap<>();
    }

    public List<Book> searchAllBooks(
            String authorName,
            Integer maxCost
    ) {
        return bookMap.values()
                .stream()
                .filter(book -> authorName == null || book.authorName().equals(authorName))
                .filter(book ->  maxCost == null || book.cost() <= maxCost)
                .toList();
    }

    public Book createBook(Book bookToCrete) {
        long newId = ++idCounter;
        Book newBook = new Book(
                newId,
                bookToCrete.name(),
                bookToCrete.authorName(),
                bookToCrete.publicationYear(),
                bookToCrete.pageNumber(),
                bookToCrete.cost()
        );
        bookMap.put(newId, newBook);
        return newBook;
    }

    public Book findById(Long id) {
        return Optional.ofNullable(bookMap.get(id))
                .orElseThrow(() -> new RuntimeException(
                        "No found book by id=%s".formatted(id)
                ));
    }

    public void deleteBook(Long id) {
        if (bookMap.remove(id) == null) {
            throw new RuntimeException("No found book by id=%s".formatted(id));
        }
    }

    public Book updateBook(
            Long id,
            Book bookToUpdate
    ) {
        if (bookMap.get(id) == null) {
            throw new RuntimeException("No found book by id=%s".formatted(id));
        }
        Book updatedBook = new Book(
                id,
                bookToUpdate.name(),
                bookToUpdate.authorName(),
                bookToUpdate.publicationYear(),
                bookToUpdate.pageNumber(),
                bookToUpdate.cost()
        );
        bookMap.put(id, updatedBook);
        return updatedBook;
    }

//    public List<Book> searchAllBooks(
//            String authorName,
//            Integer maxCost
//    ) {
//        return bookMap.values()
//                .stream()
//                .filter(book -> authorName == null || book.authorName().equals(authorName))
//                .filter(book -> maxCost == null || book.cost() < maxCost)
//                .toList();
//    }

//    public Book createBook(Book bookToCrete) {
//        var newId = ++idCounter;
//        var newBook = new Book(
//                newId,
//                bookToCrete.name(),
//                bookToCrete.authorName(),
//                bookToCrete.publicationYear(),
//                bookToCrete.pageNumber(),
//                bookToCrete.cost()
//        );
//        bookMap.put(newId, newBook);
//        return newBook;
//    }
//
//    public Book findById(Long id) {
//        return Optional.ofNullable(bookMap.get(id))
//                .orElseThrow(() -> new RuntimeException(
//                        "No found book by id=%s".formatted(id)
//                ));
//    }
//
//    public void deleteBook(Long id) {
//        var result = bookMap.remove(id);
//        if (result == null) {
//            throw new RuntimeException("No found book by id=%s".formatted(id));
//        }
//    }
//
//    public Book updateBook(
//            Long id,
//            Book bookToUpdate
//    ) {
//        if (bookMap.get(id) == null) {
//            throw new RuntimeException("No found book by id=%s".formatted(id));
//        }
//        var updatedBook = new Book(
//                id,
//                bookToUpdate.name(),
//                bookToUpdate.authorName(),
//                bookToUpdate.publicationYear(),
//                bookToUpdate.pageNumber(),
//                bookToUpdate.cost()
//        );
//        bookMap.put(id, updatedBook);
//        return updatedBook;
//    }
}
