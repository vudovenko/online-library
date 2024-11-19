package dev.vudovenko.onlinelibrary.book;

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
    private final BookDtoConverter dtoConverter;

    @GetMapping("/books")
    public List<BookDto> getAllBooks(
            @Valid BookSearchFilter bookSearchFilter
    ) {
        LOG.info("Get request for getAllBooks");
        return bookService.searchAllBooks(bookSearchFilter)
                .stream()
                .map(dtoConverter::toDto)
                .toList();
    }

    @PostMapping("/books")
    public ResponseEntity<BookDto> createBook(
            @RequestBody @Valid BookDto bookDtoToCrete
    ) {
        LOG.info("Get request for createBook: book={}", bookDtoToCrete);
        Book createdBook = bookService.createBook(
                dtoConverter.toDomain(bookDtoToCrete)
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(createdBook));
    }

    @GetMapping("/books/{id}")
    public BookDto findById(
            @PathVariable("id") Long id
    ) {
        LOG.info("Get request for findById: id={}", id);
        return dtoConverter.toDto(bookService.findById(id));
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
    public BookDto updateBook(
            @PathVariable("id") Long id,
            @RequestBody @Valid BookDto bookDtoToUpdate
    ) {
        LOG.info("Get request for update book: id={}, bookToUpdate={}",
                id, bookDtoToUpdate);

        Book updatedBook = bookService.updateBook(
                id,
                dtoConverter.toDomain(bookDtoToUpdate)
        );

        return dtoConverter.toDto(updatedBook);
    }
}
