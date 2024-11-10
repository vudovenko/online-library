package dev.vudovenko.onlinelibrary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private static final Logger LOG = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    @GetMapping("/books")
    public List<Book> getAllBooks(
            @RequestParam(name = "authorName", required = false) String authorName,
            @RequestParam(name = "maxCost", required = false) Integer maxCost
    ) {
        LOG.info("Get request for getAllBooks");
        return bookService.searchAllBooks(authorName, maxCost);
    }

    @PostMapping("/books")
    public ResponseEntity<Book> createBook(
            @RequestBody @Valid Book bookToCrete
    ) {
        LOG.info("Get request for createBook: book={}", bookToCrete);
        Book createdBook = bookService.createBook(bookToCrete);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("my-header", "123")
                .body(createdBook);
    }

    @GetMapping("/books/{id}")
    public Book findById(
            @PathVariable("id") Long id
    ) {
        LOG.info("Get request for findById: id={}", id);
        return bookService.findById(id);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") Long id
    ) {
        LOG.info("Get request for deleteById: id={}", id);
        bookService.deleteBook(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/books/{id}")
    public Book updateBook(
            @PathVariable("id") Long id,
            @RequestBody @Valid Book bookToUpdate
    ) {
        LOG.info("Get request for update book: id={}, bookToUpdate={}",
                id, bookToUpdate);

        return bookService.updateBook(id, bookToUpdate);
    }
}
