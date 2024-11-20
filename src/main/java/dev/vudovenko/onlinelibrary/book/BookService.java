package dev.vudovenko.onlinelibrary.book;

import dev.vudovenko.onlinelibrary.author.AuthorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final BookEntityConverter entityConverter;

    public List<Book> searchAllBooks(BookSearchFilter bookSearchFilter) {
        int pageSize = bookSearchFilter.pageSize() != null
                ? bookSearchFilter.pageSize()
                : 3;
        int pageNumber = bookSearchFilter.pageNumber() != null
                ? bookSearchFilter.pageNumber()
                : 1;

        Pageable pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNumber);

        return bookRepository.searchBooks(
                        bookSearchFilter.authorId(),
                        bookSearchFilter.maxCost(),
                        pageable
                )
                .stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public Book createBook(Book bookToCrete) {
        checkAuthorExistence(bookToCrete.authorId());

        BookEntity savedEntity = bookRepository.save(
                entityConverter.toEntity(bookToCrete)
        );

        return entityConverter.toDomain(savedEntity);
    }

    public Book findById(Long id) {
        BookEntity foundBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No found book by id=%s".formatted(id)
                ));

        return entityConverter.toDomain(foundBook);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("No found book by id=%s".formatted(id));
        }
        bookRepository.deleteById(id);
    }

    public Book updateBook(
            Long id,
            Book bookToUpdate
    ) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("No found book by id=%s".formatted(id));
        }
        checkAuthorExistence(bookToUpdate.authorId());

        bookRepository.updateBook(
                id,
                bookToUpdate.name(),
                bookToUpdate.authorId(),
                bookToUpdate.publicationYear(),
                bookToUpdate.pageNumber(),
                bookToUpdate.cost()
        );
        return entityConverter.toDomain(bookRepository.findById(id).orElseThrow());
    }

    private void checkAuthorExistence(Long authorId) {
        if (!authorService.isAuthorExistsById(authorId)) {
            throw new IllegalArgumentException("Author not exists by id=%s"
                    .formatted(authorId));
        }
    }
}
