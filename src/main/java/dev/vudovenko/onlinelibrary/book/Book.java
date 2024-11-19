package dev.vudovenko.onlinelibrary.book;

public record Book(

        Long id,
        String name,
        String authorName,
        Integer publicationYear,
        Integer pageNumber,
        Integer cost
) {
}