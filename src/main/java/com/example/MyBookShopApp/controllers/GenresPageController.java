package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.GenresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GenresPageController {
    private final GenresService genresService;
    private final BookService bookService;

    @Autowired
    public GenresPageController(GenresService genresService,
                                BookService bookService) {
        this.genresService = genresService;
        this.bookService = bookService;
    }

    @GetMapping("/genres")
    public ModelAndView genresPage() {
        ModelAndView modelAndView = new ModelAndView("/genres/index");
        modelAndView.addObject("genres", genresService.getGenres());

        return modelAndView;
    }

    @GetMapping("/genres/{slug}")
    public ModelAndView slugPage(@PathVariable("slug") String slug) {
        ModelAndView modelAndView = new ModelAndView("/genres/slug");
        modelAndView.addObject("genre", genresService.getGenre(slug));
        modelAndView.addObject("parentTitles", genresService.getParentTitleGenres(slug));
        Page<Book> bookPage = genresService.getBooksByGenre(slug, 0, 20);
        modelAndView.addObject("booksByGenre", bookPage);

        return modelAndView;
    }
}
