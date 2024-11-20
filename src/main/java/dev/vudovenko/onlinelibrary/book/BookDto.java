package dev.vudovenko.onlinelibrary.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookDto(

        @Null
        Long id,

        @Size(max = 30)
        @NotBlank
        String name,

        @NotNull
        Long authorId,

        @Min(0)
        @NotNull
        @JsonProperty("pubYear")
        Integer publicationYear,

        @Min(1)
        @Max(10000)
        @NotNull
        @JsonProperty("pageNum")
        Integer pageNumber,

        @Min(0)
        @Max(100000)
        @NotNull
        Integer cost
) {
}
