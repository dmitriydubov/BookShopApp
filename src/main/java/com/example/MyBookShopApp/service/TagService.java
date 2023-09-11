package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.Tag;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final BookRepository bookRepository;
    private final TagRepository tagRepository;
    private final CalculateDiscountService calculateDiscountService;
    private final AuthorService authorService;

    @Autowired
    public TagService(BookRepository bookRepository,
                      TagRepository tagRepository,
                      CalculateDiscountService calculateDiscountService,
                      AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.tagRepository = tagRepository;
        this.calculateDiscountService = calculateDiscountService;
        this.authorService = authorService;
    }

    public Map<Tag, String> getTags() {
        return tagRepository.findAll()
            .stream()
            .collect(Collectors.toMap(tag -> tag, tag -> getClassSize(bookRepository.findBookByTag(tag).size())));
    }

    public Tag getTagByTagName(String tagName) {
        return tagRepository.findByTag(tagName);
    }

    public Page<Book> getBookByTag(Integer offset, Integer limit, String tagName) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = authorService.setBookAuthor(
                bookRepository.findBookByTag(tagRepository.findByTag(tagName), nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Page<Book> getBooksByTagId(Integer tagId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = authorService.setBookAuthor(
                bookRepository.findBookByTag(tagRepository.findById(tagId).orElse(new Tag()), nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    private String getClassSize(Integer size) {
        String lg = "Tag Tag_lg";
        String md = "Tag Tag_md";
        String sm = "Tag Tag_sm";
        String xs = "Tag Tag_xs";

        if (size > 130) {
            return lg;
        } else if ( size < 130 && size >= 110) {
            return md;
        } else if (size < 110 && size >= 80) {
            return sm;
        } else {
            return xs;
        }
    }
}
