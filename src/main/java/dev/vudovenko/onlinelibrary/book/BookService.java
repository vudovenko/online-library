package dev.vudovenko.onlinelibrary.book;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookEntityConverter entityConverter;

    public BookService(
            BookRepository bookRepository,
            BookEntityConverter entityConverter
    ) {
        this.bookRepository = bookRepository;
        this.entityConverter = entityConverter;
    }

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
                        bookSearchFilter.authorName(),
                        bookSearchFilter.maxCost(),
                        pageable
                )
                .stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public Book createBook(Book bookToCrete) {
        BookEntity bookToSave = new BookEntity(
                null,
                bookToCrete.name(),
                bookToCrete.authorName(),
                bookToCrete.publicationYear(),
                bookToCrete.pageNumber(),
                bookToCrete.cost()
        );
        BookEntity savedEntity = bookRepository.save(bookToSave);

        return new Book(
                savedEntity.getId(),
                savedEntity.getName(),
                savedEntity.getAuthorName(),
                savedEntity.getPublicationYear(),
                bookToSave.getPageNumber(),
                bookToSave.getCost()
        );
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
        bookRepository.updateBook(
                id,
                bookToUpdate.name(),
                bookToUpdate.authorName(),
                bookToUpdate.publicationYear(),
                bookToUpdate.pageNumber(),
                bookToUpdate.cost()
        );
        return entityConverter.toDomain(bookRepository.findById(id).orElseThrow());
    }
}
