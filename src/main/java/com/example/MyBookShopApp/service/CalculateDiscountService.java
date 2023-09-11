package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateDiscountService {
    public static Page<Book> calculateDiscount(Page<Book> bookPage) {
        bookPage.forEach(book -> {
            if (book.getDiscount() != 0) {
                Integer oldPrice = book.getPrice();
                Integer discount = book.getDiscount();
                book.setDiscountPrice(oldPrice - (oldPrice * discount / 100));
            }
        });
        return bookPage;
    }

    public static Book calculateDiscount(Book book) {
        if (book.getDiscount() != 0) {
            Integer oldPrice = book.getPrice();;
            Integer discount = book.getDiscount();
            book.setDiscountPrice(oldPrice - (oldPrice * discount / 100));
        }
        return book;
    }

    public static List<Book> calculateDiscount(List<Book> bookList) {
        bookList.forEach(book -> {
            if (book.getDiscount() != 0) {
                Integer oldPrice = book.getPrice();
                Integer discount = book.getDiscount();
                book.setDiscountPrice(oldPrice - (oldPrice * discount / 100));
            }
        });
        return bookList;
    }
}
