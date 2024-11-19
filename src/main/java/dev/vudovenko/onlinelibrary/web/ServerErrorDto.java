package dev.vudovenko.onlinelibrary.web;

import java.time.LocalDateTime;

public record ServerErrorDto(

        String message,
        String detailedMessage,
        LocalDateTime dateTime
) {
}
