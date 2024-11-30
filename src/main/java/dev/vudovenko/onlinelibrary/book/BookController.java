package dev.vudovenko.onlinelibrary.book;

import dev.vudovenko.onlinelibrary.book.purchase.PurchaseService;
import dev.vudovenko.onlinelibrary.security.jwt.AuthenticationService;
import dev.vudovenko.onlinelibrary.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookDtoConverter dtoConverter;
    private final AuthenticationService authenticationService;
    private final PurchaseService purchaseService;

    @GetMapping("/books")
    public List<BookDto> getAllBooks(
            @Valid BookSearchFilter bookSearchFilter
    ) {
        log.info("Get request for getAllBooks");
        return bookService.searchAllBooks(bookSearchFilter)
                .stream()
                .map(dtoConverter::toDto)
                .toList();
    }

    @PostMapping("/books")
    public ResponseEntity<BookDto> createBook(
            @RequestBody @Valid BookDto bookDtoToCrete
    ) {
        log.info("Get request for createBook: book={}", bookDtoToCrete);
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
        log.info("Get request for findById: id={}", id);
        return dtoConverter.toDto(bookService.findById(id));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for deleteById: id={}", id);
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
        log.info("Get request for update book: id={}, bookToUpdate={}",
                id, bookDtoToUpdate);

        Book updatedBook = bookService.updateBook(
                id,
                dtoConverter.toDomain(bookDtoToUpdate)
        );

        return dtoConverter.toDto(updatedBook);
    }

    @PostMapping("/books/{id}/purchase")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> purchaseBook(
            @PathVariable("id") Long bookId
    ) {
        log.info("Get request for book purchase: bookId={}", bookId);

        User user = authenticationService.getCurrentAuthenticatedUserOrThrow();

        purchaseService.performBookPurchase(user, bookId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
