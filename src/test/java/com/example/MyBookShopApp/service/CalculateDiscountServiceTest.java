package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateDiscountServiceTest {
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setPrice(1000);
        book.setDiscount(15);
    }

    @Test
    void calculateDiscount() {
        CalculateDiscountService.calculateDiscount(book);
        assertEquals(850, book.getDiscountPrice());
    }
}