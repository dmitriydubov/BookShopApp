package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceService {

    public Integer getTotalPriceWithoutDiscount(List<Book> bookList) {
        return bookList.stream()
            .map(Book::getPrice)
            .reduce(0, Integer::sum);
    }

    public Integer getTotalPriceWithDiscount(List<Book> bookList) {
        return bookList.stream()
            .map(book -> CalculateDiscountService.calculateDiscount(book).getDiscountPrice())
            .reduce(Integer::sum).orElseThrow();
    }
}
