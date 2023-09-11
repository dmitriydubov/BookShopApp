package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class TagsPageController {
    private final BookService bookService;
    private final TagService tagService;

    @Autowired
    public TagsPageController(BookService bookService, TagService tagService) {
        this.bookService = bookService;
        this.tagService = tagService;
    }

    @GetMapping("/tags/{tag}")
    public ModelAndView tagsPage(@PathVariable(value = "tag") String tagName) {
        ModelAndView modelAndView = new ModelAndView("/tags/index");
        List<Book> bookList = tagService.getBookByTag(0, 20, tagName).getContent();
        modelAndView.addObject("currentTag", tagService.getTagByTagName(tagName));
        modelAndView.addObject("bookList", bookList);
        return modelAndView;
    }
}
