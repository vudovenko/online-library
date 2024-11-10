package dev.vudovenko.onlinelibrary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookService bookService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSuccessCreateBook() throws Exception {
        Book book = new Book(
                null,
                "some-book-a",
                "Vlad",
                2024,
                100,
                6000
        );

        String bookJson = objectMapper.writeValueAsString(book);

        String createdBookJson = mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson)
                )
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Book bookResponse = objectMapper.readValue(createdBookJson, Book.class);

        Assertions.assertNotNull(bookResponse.id());
        org.assertj.core.api.Assertions.assertThat(bookResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(book);
    }

    @Test
    void shouldNotCreateBookWhenRequestNotValid() throws Exception {
        Book book = new Book(
                null,
                null,
                "Vlad",
                2024,
                100,
                6000
        );

        String bookJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson)
                )
                .andExpect(status().is(400));
    }

    @Test
    public void shouldSuccessSearchBookById() throws Exception {
        Book book = new Book(
                null,
                "some-book-a",
                "Vlad",
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

        Book foundBook = objectMapper.readValue(foundBookJson, Book.class);
        Assertions.assertEquals(book, foundBook);
    }

    @Test
    public void shouldReturnNotFoundWhenNotPresent() throws Exception {
        mockMvc.perform(get("/books/{id}", Integer.MAX_VALUE))
                .andExpect(status().is(404));
    }
}