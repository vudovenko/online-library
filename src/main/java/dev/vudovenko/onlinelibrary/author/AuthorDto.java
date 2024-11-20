package dev.vudovenko.onlinelibrary.author;

import dev.vudovenko.onlinelibrary.book.BookDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthorDto(

        @Null
        Long id,
        @NotBlank
        String name,
        @Min(0)
        Integer birthYear,
        @Size(min = 0)
        List<BookDto> books
) {
}
