package dev.vudovenko.onlinelibrary.author;

import dev.vudovenko.onlinelibrary.book.Book;

import java.util.List;

public record Author(
        Long id,
        String name,
        Integer birthYear,
        List<Book> books
) {
}