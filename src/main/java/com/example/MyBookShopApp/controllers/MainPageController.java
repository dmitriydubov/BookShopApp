package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.Tag;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.dto.SearchWordDto;
import com.example.MyBookShopApp.service.BooksRatingAndPopularityService;
import com.example.MyBookShopApp.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final TagService tagService;

    @Autowired
    public MainPageController(BookService bookService,
                              BooksRatingAndPopularityService booksRatingAndPopularityService,
                              TagService tagService) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.tagService = tagService;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks() {
        return bookService.getPageOfRecommendedBooks(0, 6).getContent();
    }

    @ModelAttribute("recentBooks")
    public List<Book> recentBooks() {
        return bookService.getPageOfRecentBooks(0, 6).getContent();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResults")
    public List<Book> searchResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks() {
        return booksRatingAndPopularityService.getPageOfPopularBooks(0, 6).getContent();
    }

    @ModelAttribute("tags")
    public Map<Tag, String> tags() {
        return tagService.getTags();
    }

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                   Model model) {
        model.addAttribute("searchWordDto", searchWordDto);
        model.addAttribute("searchResults",
                bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 5).getContent());
        return "/search/index";
    }
}
