package dev.vudovenko.onlinelibrary.book;

import org.springframework.stereotype.Component;

@Component
public class BookEntityConverter {

    public BookEntity toEntity(Book book) {
        return new BookEntity(
                book.id(),
                book.name(),
                book.authorId(),
                book.publicationYear(),
                book.pageNumber(),
                book.cost()
        );
    }

    public Book toDomain(BookEntity book) {
        return new Book(
                book.getId(),
                book.getName(),
                book.getAuthorId(),
                book.getPublicationYear(),
                book.getPageNumber(),
                book.getCost()
        );
    }

}
