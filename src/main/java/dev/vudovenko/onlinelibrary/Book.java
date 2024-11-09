package dev.vudovenko.onlinelibrary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Book(

        Long id,
        String name,
        String authorName,
        @JsonProperty("pubYear")
        Integer publicationYear,
        @JsonProperty("pageNum")
        Integer pageNumber,
        Integer cost
) {
}
