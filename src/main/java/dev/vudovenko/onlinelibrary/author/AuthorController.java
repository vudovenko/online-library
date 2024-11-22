package dev.vudovenko.onlinelibrary.author;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;
    private final AuthorDtoConverter authorDtoConverter;

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(
            @RequestBody @Valid AuthorDto authorToCreate
    ) {
        LOGGER.info("Get request for create author: author = {}", authorToCreate);
        Author createdAuthor = authorService.createAuthor(authorDtoConverter.toDomain(authorToCreate));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authorDtoConverter.toDto(createdAuthor));
    }

    @GetMapping
    public List<AuthorDto> getAllAuthors() {
        LOGGER.info("Get request for get all authors");
        return authorService.getAllAuthors()
                .stream()
                .map(authorDtoConverter::toDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @PathVariable("id") Long authorId
    ) {
        LOGGER.info("Get request for delete author: authorId = {}", authorId);
        authorService.deleteAuthor(authorId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
