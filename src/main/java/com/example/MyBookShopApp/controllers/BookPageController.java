package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.security.service.AuthService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.RatingService;
import com.example.MyBookShopApp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BookPageController {
    private final BookService bookService;
    private final RatingService ratingService;
    private final ReviewService reviewService;
    private final AuthService authService;

    @Autowired
    public BookPageController(BookService bookService,
                              RatingService ratingService,
                              ReviewService reviewService, AuthService authService) {
        this.bookService = bookService;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @GetMapping("/books/{slug}")
    public ModelAndView bookPage(@PathVariable("slug") String slug,
                                 @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                 @CookieValue(value = "cartContents", required = false) String cartContents) {
        ModelAndView modelAndView = new ModelAndView("/books/slug");
        Book book = bookService.getBookBySlug(slug);
        ratingService.setBookRatingStars(book);
        modelAndView.addObject("book", book);
        modelAndView.addObject("allRatings", ratingService.getAllRatings(book));
        modelAndView.addObject("allRatingsByScore", ratingService.getAllRatingsByScore(book));
        modelAndView.addObject("reviewList", reviewService.getAllReviewsByBook(book));
        modelAndView.addObject("reviewSizeValue", reviewService.getReviewSizeValue(book));
        modelAndView.addObject("isKeptBook", bookService.checkBookIsKeptOrCart(postponedContents, slug));
        modelAndView.addObject("isBookInCart", bookService.checkBookIsKeptOrCart(cartContents, slug));
        modelAndView.addObject("isAuthenticated", authService.checkAuthentication(SecurityContextHolder.getContext().getAuthentication()));
        modelAndView.addObject("isPaidBook", bookService.checkIsPaidBook(book));

        return modelAndView;
    }
}
