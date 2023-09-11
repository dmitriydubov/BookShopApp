package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BooksRatingAndPopularityService {
    private final BookRepository bookRepository;
    private final CalculateDiscountService calculateDiscountService;
    private final AuthorService authorService;

    @Autowired
    public BooksRatingAndPopularityService(BookRepository bookRepository,
                                           CalculateDiscountService calculateDiscountService,
                                           AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.calculateDiscountService = calculateDiscountService;
        this.authorService = authorService;
    }

    @Transactional(readOnly = true)
    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = authorService.setBookAuthor(bookRepository.getPopularBooks(nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }
}
