package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ApiMethodsControllerTest {

    private String[] booksIds;
    private String bookSlug;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @BeforeEach
    void setUp() {
        booksIds = new String[]{"70"};
        bookSlug = generateSlug(booksIds);
    }

    @Test
    void addBookToCart() throws Exception {
        mockMvc.perform(post("/api/changeBookStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .param("booksIds[]", booksIds)
                .param("status", "CART"))
            .andExpect(cookie().exists("cartContents"))
            .andExpect(cookie().value("cartContents", bookSlug))
            .andExpect(status().isOk());
    }

    @Test
    void removeBookFromCart() throws Exception {
        Cookie cartContents = new Cookie("cartContents", bookSlug);

        mockMvc.perform(post("/api/changeBookStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cartContents)
                .param("booksIds[]", booksIds)
                .param("status", "UNLINK"))
            .andExpect(cookie().value("cartContents", ""))
            .andExpect(status().isOk());
    }

    private String generateSlug(String[] booksIds) {
        List<String> bookIdList = Arrays.asList(booksIds);

        return bookIdList.stream()
            .map(bookId -> bookIdList.indexOf(bookId) == bookIdList.size() - 1 ?
                bookService.getBookById(Integer.valueOf(bookId)).getSlug() :
                bookService.getBookById(Integer.valueOf(bookId)).getSlug() + "/")
            .collect(Collectors.joining());
    }
}