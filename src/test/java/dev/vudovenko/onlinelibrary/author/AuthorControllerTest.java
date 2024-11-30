package dev.vudovenko.onlinelibrary.author;

import dev.vudovenko.onlinelibrary.AbstractTest;
import dev.vudovenko.onlinelibrary.book.Book;
import dev.vudovenko.onlinelibrary.book.BookService;
import dev.vudovenko.onlinelibrary.users.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorControllerTest extends AbstractTest {

    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldSuccessCreateAuthor() throws Exception {
        AuthorDto author = new AuthorDto(
                null,
                "some-author" + getRandomInt(),
                1900,
                List.of()
        );

        String authorJson = objectMapper.writeValueAsString(author);

        String createdAuthorJson = mockMvc.perform(
                        post("/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(authorJson)
                                .header("Authorization", getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthorDto authorDtoResponse = objectMapper.readValue(createdAuthorJson, AuthorDto.class);

        Assertions.assertNotNull(authorDtoResponse.id());
        org.assertj.core.api.Assertions.assertThat(authorDtoResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(author);
        Assertions.assertTrue(authorRepository.existsById(authorDtoResponse.id()));
    }

    @Test
    void shouldSuccessDeleteAuthor() throws Exception {
        Author author = authorService.createAuthor(
                new Author(
                        null,
                        "author-name" + getRandomInt(),
                        1900,
                        List.of()
                )
        );
        List<Book> authorBooks = IntStream.range(0, 10)
                .mapToObj(i -> createBootToAuthor(author.id()))
                .toList();

        mockMvc.perform(
                        delete("/authors/{id}", author.id())
                                .header("Authorization", getAuthorizationHeader(UserRole.ADMIN))
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(authorRepository.existsById(author.id()));
        authorBooks
                .forEach(book -> {
                    Book updatedBook = bookService.findById(book.id());
                    Assertions.assertNull(updatedBook.authorId());
                });
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToDeleteAuthor() throws Exception {
        mockMvc.perform(
                        delete("/authors/{id}", 1L)
                                .header("Authorization", getAuthorizationHeader(UserRole.USER))
                )
                .andExpect(status().isForbidden());
    }

    public Book createBootToAuthor(Long authorId) {
        return bookService.createBook(
                new Book(
                        null,
                        "book-name" + getRandomInt(),
                        authorId,
                        2024,
                        100,
                        6000
                )
        );
    }
}