package dev.vudovenko.onlinelibrary.book;

import org.springframework.stereotype.Component;

@Component
public class BookDtoConverter {

    public BookDto toDto(Book book) {
        return new BookDto(
                book.id(),
                book.name(),
                book.authorId(),
                book.publicationYear(),
                book.pageNumber(),
                book.cost()
        );
    }

    public Book toDomain(BookDto bookDto) {
        return new Book(
                bookDto.id(),
                bookDto.name(),
                bookDto.authorId(),
                bookDto.publicationYear(),
                bookDto.pageNumber(),
                bookDto.cost()
        );
    }
}
