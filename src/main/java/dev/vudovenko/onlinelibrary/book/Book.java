package dev.vudovenko.onlinelibrary.book;

public record Book(

        Long id,
        String name,
        Long authorId,
        Integer publicationYear,
        Integer pageNumber,
        Integer cost
) {
}