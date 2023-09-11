package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.repository.AuthorRepository;
import com.example.MyBookShopApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CalculateDiscountService calculateDiscountService;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository, CalculateDiscountService calculateDiscountService) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.calculateDiscountService = calculateDiscountService;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = authorRepository.findAll();
        authors.forEach(author -> {
            String firstName = author.getName().split(" ")[0];
            String lastName = author.getName().split(" ")[1];
            author.setFirstName(firstName);
            author.setLastName(lastName);
        });

        return authors.stream().collect(Collectors.groupingBy((Author a) -> getLastName(a.getName()).substring(0,1)));
    }

    public Page<Book> setBookAuthor(Page<Book> bookPage) {
        bookPage.forEach(book -> book.setAuthor(authorRepository.findByBookId(book.getId())));
        return bookPage;
    }

    public List<Book> setBookAuthor(List<Book> bookList) {
        bookList.forEach(book -> book.setAuthor(authorRepository.findByBookId(book.getId())));
        return bookList;
    }

    public Page<Book> getAuthorBooks(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = setBookAuthor(bookRepository.findByAuthorSlug(slug, nextPage));

        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Author getAuthorBySlug(String slug) {
        return authorRepository.findBySlug(slug);
    }

    public List<List<String>> getParagraphList(String slug) {
        Author author = authorRepository.findBySlug(slug);
        String[] sentencesArr = author.getDescription().split("[.!?]\s+");

        List<String> result = new ArrayList<>();
        StringBuilder paragraphBuilder = new StringBuilder();
        int sentenceCount = 0;

        for (String sentence : sentencesArr) {
            if (sentenceCount < 4) {
                paragraphBuilder.append(sentence).append(" ");
                sentenceCount++;
            } else {
                result.add(paragraphBuilder.toString().trim());
                paragraphBuilder = new StringBuilder();
                sentenceCount = 0;
            }
        }

        if (sentenceCount > 0) {
            result.add(paragraphBuilder.toString().trim());
        }

        List<String> visibleParagraphs;
        List<String> hiddenParagraphs;

        if (result.size() > 2) {
            visibleParagraphs = result.subList(0, 2);
            hiddenParagraphs = result.subList(2, result.size() - 1);
        } else {
            visibleParagraphs = result;
            hiddenParagraphs = new ArrayList<>();
        }

        return new ArrayList<>(){{
            add(visibleParagraphs);
            add(hiddenParagraphs);
        }};
    }

    public Page<Book> getBooksByAuthorId(Integer id, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = setBookAuthor(bookRepository.findByAuthorId(id, nextPage));

        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Integer getBooksCount(String slug) {
        return bookRepository.getCountOfBooksByAuthorSlug(slug);
    }

    private String getLastName(String fullName) {
        String[] strArr = fullName.split(" ");
        return strArr[1];
    }

    public Author getAuthorByBookSlug(String slug) {
        return authorRepository.findByBookSlug(slug);
    }
}
