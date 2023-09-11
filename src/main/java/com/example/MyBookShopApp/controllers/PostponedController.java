package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class PostponedController {
    private final BookService bookService;
    private final RatingService ratingService;

    @Autowired
    public PostponedController(BookService bookService, RatingService ratingService) {
        this.bookService = bookService;
        this.ratingService = ratingService;
    }

    @ModelAttribute(name = "postponedBooks")
    public List<Book> postponedBooks() {
        return new ArrayList<>();
    }

    @GetMapping("/postponed")
    public String handlePostponed(@CookieValue(value = "postponedContents", required = false) String postponedContents,
                                  Model model) {

        if (postponedContents != null) {
            postponedContents = postponedContents.startsWith("/")
                    ? postponedContents.substring(1) : postponedContents;
            postponedContents = postponedContents.endsWith("/")
                    ? postponedContents.substring(0, postponedBooks().size() - 1) : postponedContents;
            String[] cookieSlugs = postponedContents.split("/");
            List<Book> booksFromCookieSlug = bookService.getBySlugIn(cookieSlugs);
            booksFromCookieSlug.forEach(ratingService::setBookRatingStars);

            Integer[] booksIds = booksFromCookieSlug.stream().map(Book::getId).toArray(Integer[]::new);

            model.addAttribute("postponedBooks", booksFromCookieSlug);
            model.addAttribute("booksIds", Arrays.toString(booksIds));
        }

        return "postponed";
    }
}
