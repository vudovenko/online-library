package dev.vudovenko.onlinelibrary;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vudovenko.onlinelibrary.book.BookDto;
import dev.vudovenko.onlinelibrary.book.BookService;
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
class BookDtoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookService bookService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSuccessCreateBook() throws Exception {
        BookDto bookDto = new BookDto(
                null,
                "some-book-a",
                "Vlad",
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
    }

    @Test
    void shouldNotCreateBookWhenRequestNotValid() throws Exception {
        BookDto bookDto = new BookDto(
                null,
                null,
                "Vlad",
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
        BookDto bookDto = new BookDto(
                null,
                "some-book-a",
                "Vlad",
                2024,
                100,
                6000
        );
        bookDto = bookService.createBook(bookDto);

        String foundBookJson = mockMvc.perform(get("/books/{id}", bookDto.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto foundBookDto = objectMapper.readValue(foundBookJson, BookDto.class);
        Assertions.assertEquals(bookDto, foundBookDto);
    }

    @Test
    public void shouldReturnNotFoundWhenNotPresent() throws Exception {
        mockMvc.perform(get("/books/{id}", Integer.MAX_VALUE))
                .andExpect(status().is(404));
    }
}