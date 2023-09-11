package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.SearchWordDto;
import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.service.AuthorService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@Api(description = "authors data")
public class AuthorsController {

    private final AuthorService authorService;

    @Autowired
    public AuthorsController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<Author>> authorsMap(){
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors")
    public String authorsPage(){
        return "/authors/index";
    }

    @GetMapping("/authors/{slug}")
    public ModelAndView authorPage(@PathVariable("slug") String slug) {
        ModelAndView modelAndView = new ModelAndView("/authors/slug");
        modelAndView.addObject("author", authorService.getAuthorBySlug(slug));
        modelAndView.addObject("descriptionParagraphs", authorService.getParagraphList(slug));
        modelAndView.addObject("booksCount", authorService.getBooksCount(slug));
        modelAndView.addObject(
                "authorBooks", authorService.getAuthorBooks(slug, 0, 10).getContent());

        return modelAndView;
    }
}
