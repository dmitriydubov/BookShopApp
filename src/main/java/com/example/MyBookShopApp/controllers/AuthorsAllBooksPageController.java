package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthorsAllBooksPageController {
    private final AuthorService authorService;
    private final BookService bookService;

    @Autowired
    public AuthorsAllBooksPageController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("/books/author/{slug}")
    public ModelAndView authorAllBookPage(@PathVariable("slug") String slug) {
        ModelAndView modelAndView = new ModelAndView("/books/author");
        modelAndView.addObject("author", authorService.getAuthorBySlug(slug));
        modelAndView.addObject("books", bookService.getBooksByAuthorSlug(slug, 0, 20));

        return modelAndView;
    }
}
