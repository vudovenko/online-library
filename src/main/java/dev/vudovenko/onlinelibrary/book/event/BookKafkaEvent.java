package dev.vudovenko.onlinelibrary.book.event;

import dev.vudovenko.onlinelibrary.book.Book;

public record BookKafkaEvent(

        Long bookId,
        EventType eventType,
        Book book
) {
}
