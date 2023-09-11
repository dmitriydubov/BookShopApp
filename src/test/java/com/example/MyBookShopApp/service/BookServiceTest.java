package com.example.MyBookShopApp.service;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.Rating;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookServiceTest {
    private int offset;
    private int limit;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    BookServiceTest(BookService bookService, BookRepository bookRepository, RatingRepository ratingRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.ratingRepository = ratingRepository;
    }

    @BeforeEach
    void setUp() {
        offset = 0;
        limit = 6;
    }

    @Test
    @Transactional
    void getPageOfRecommendedBooks() {
        if (ratingRepository.findAll().size() <= 3) {
            List<Book> bookList = bookRepository.findBooksByIds(List.of(10, 23, 35));
            bookList.forEach(book -> {
                Rating rating = new Rating();
                rating.setBook(book);
                rating.setRating((int) Math.round(Math.random() * 5));
                ratingRepository.saveAndFlush(rating);
            });
        }
        Page<Book> bookPage = bookService.getPageOfRecommendedBooks(offset, limit);
        int firstBookRating = bookPage.getContent().get(0).getBookRating().get(0).getRating();
        int secondBookRating = bookPage.getContent().get(1).getBookRating().get(0).getRating();
        int thirdBookRating = bookPage.getContent().get(2).getBookRating().get(0).getRating();
        boolean result = firstBookRating >= secondBookRating && secondBookRating >= thirdBookRating;
        assertTrue(result);
    }
}