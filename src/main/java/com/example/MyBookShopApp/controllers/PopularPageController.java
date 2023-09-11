package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BooksRatingAndPopularityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class PopularPageController {
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;

    @Autowired
    public PopularPageController(BooksRatingAndPopularityService booksRatingAndPopularityService) {
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
    }

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks() {
        return booksRatingAndPopularityService.getPageOfPopularBooks(0, 20).getContent();
    }

    @GetMapping("/books/popular")
    public String popularPage() {
        return "/books/popular";
    }
}
