package dev.vudovenko.onlinelibrary.author;

import dev.vudovenko.onlinelibrary.book.BookEntityConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthorEntityConverter {
    private final BookEntityConverter bookEntityConverter;

    public AuthorEntityConverter(BookEntityConverter bookEntityConverter) {
        this.bookEntityConverter = bookEntityConverter;
    }

    public Author toDomain(AuthorEntity authorEntity) {
        return new Author(
                authorEntity.getId(),
                authorEntity.getName(),
                authorEntity.getBirthYear(),
                authorEntity.getBooks().stream()
                        .map(bookEntityConverter::toDomain)
                        .toList()
        );
    }

    public AuthorEntity toEntity(Author author) {
        return new AuthorEntity(
                author.id(),
                author.name(),
                author.birthYear(),
                author.books().stream()
                        .map(bookEntityConverter::toEntity)
                        .collect(Collectors.toSet())
        );
    }
}
