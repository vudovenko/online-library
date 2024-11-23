package dev.vudovenko.onlinelibrary.books;

import dev.vudovenko.onlinelibrary.AbstractTest;
import dev.vudovenko.onlinelibrary.author.Author;
import dev.vudovenko.onlinelibrary.author.AuthorService;
import dev.vudovenko.onlinelibrary.book.Book;
import dev.vudovenko.onlinelibrary.book.BookDto;
import dev.vudovenko.onlinelibrary.book.BookRepository;
import dev.vudovenko.onlinelibrary.book.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookDtoControllerTest extends AbstractTest {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldSuccessCreateBook() throws Exception {
        Author author = createDummyAuthor();
        BookDto bookDto = new BookDto(
                null,
                "some-book" + getRandomInt(),
                author.id(),
                2024,
                100,
                6000
        );

        String bookJson = objectMapper.writeValueAsString(bookDto);

        String createdBookJson = mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson)
                )
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto bookDtoResponse = objectMapper.readValue(createdBookJson, BookDto.class);

        Assertions.assertNotNull(bookDtoResponse.id());
        org.assertj.core.api.Assertions.assertThat(bookDtoResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookDto);
        Assertions.assertTrue(bookRepository.existsById(bookDtoResponse.id()));
    }

    @Test
    void shouldNotCreateBookWhenRequestNotValid() throws Exception {
        BookDto bookDto = new BookDto(
                null,
                null,
                1L,
                2024,
                100,
                6000
        );

        String bookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson)
                )
                .andExpect(status().is(400));
    }

    @Test
    public void shouldSuccessSearchBookById() throws Exception {
        Author author = createDummyAuthor();
        Book book = new Book(
                null,
                "some-book-a" + getRandomInt(),
                author.id(),
                2024,
                100,
                6000
        );
        book = bookService.createBook(book);

        String foundBookJson = mockMvc.perform(get("/books/{id}", book.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto foundBookDto = objectMapper.readValue(foundBookJson, BookDto.class);
        org.assertj.core.api.Assertions
                .assertThat(book)
                .usingRecursiveComparison()
                .isEqualTo(foundBookDto);
    }

    @Test
    public void shouldReturnNotFoundWhenNotPresent() throws Exception {
        mockMvc.perform(get("/books/{id}", Integer.MAX_VALUE))
                .andExpect(status().is(404));
    }

    private Author createDummyAuthor() {
        return authorService.createAuthor(
                new Author(
                        null,
                        "author-" + getRandomInt(),
                        1900,
                        List.of()
                )
        );
    }
}