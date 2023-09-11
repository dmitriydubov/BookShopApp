package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BooksRatingAndPopularityServiceTest {
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private int offset;
    private int limit;

    @BeforeEach
    void setUp() {
        offset = 0;
        limit = 6;
    }

    @Autowired
    BooksRatingAndPopularityServiceTest(BooksRatingAndPopularityService booksRatingAndPopularityService) {
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
    }

    @Test
    void getPageOfPopularBooks() {
        Page<Book> bookPage = booksRatingAndPopularityService.getPageOfPopularBooks(offset, limit);
        int firstBookPopularityIndex = getBookPopularityIndex(bookPage.getContent().get(0));
        int secondBookPopularityIndex = getBookPopularityIndex(bookPage.getContent().get(1));
        int thirdBookPopularityIndex = getBookPopularityIndex(bookPage.getContent().get(2));
        boolean result = firstBookPopularityIndex >= secondBookPopularityIndex &&
                secondBookPopularityIndex >= thirdBookPopularityIndex;
        assertTrue(result);
    }

    private int getBookPopularityIndex(Book book) {
        return (int) (book.getPurchaseNumber() + (0.7 * book.getCartNumber()) + (0.4 * book.getPostponedNumber()));
    }
}