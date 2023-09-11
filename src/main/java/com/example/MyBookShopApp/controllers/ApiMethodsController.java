package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.BookReviewLikeDto;
import com.example.MyBookShopApp.dto.payment.Payment;
import com.example.MyBookShopApp.dtoAbstract.BookReviewDto;
import com.example.MyBookShopApp.dtoAbstract.ChangeBookStatusDto;
import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.dto.RedirectDto;
import com.example.MyBookShopApp.service.*;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.dto.SearchWordDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

@RestController
public class ApiMethodsController {
    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final TagService tagService;
    private final GenresService genresService;
    private final AuthorService authorService;
    private final BookStatusService bookStatusService;
    private final ChangeBookStatusService changeBookStatusService;
    private final ReviewService reviewService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    @Autowired
    public ApiMethodsController(BookService bookService,
                                BooksRatingAndPopularityService booksRatingAndPopularityService,
                                TagService tagService,
                                GenresService genresService,
                                AuthorService authorService,
                                BookStatusService bookStatusService,
                                ChangeBookStatusService changeBookStatusService,
                                ReviewService reviewService,
                                PaymentService paymentService,
                                OrderService orderService) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.tagService = tagService;
        this.genresService = genresService;
        this.authorService = authorService;
        this.bookStatusService = bookStatusService;
        this.changeBookStatusService = changeBookStatusService;
        this.reviewService = reviewService;
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @GetMapping("/api/books/recommended")
    public BooksPageDto getBooksPage(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }

    @GetMapping("/api/books/recent")
    public BooksPageDto getRecentBooksPage(@RequestParam(value = "from", required = false) String from,
                                           @RequestParam(value = "to", required = false) String to,
                                           @RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit) throws ParseException {
        if (from == null && to == null) {
            return new BooksPageDto(bookService.getPageOfRecentBooks(offset, limit).getContent());
        } else {
            return new BooksPageDto(bookService.getPageOfRecentBooks(from, to, offset, limit).getContent());
        }
    }

    @GetMapping("/api/books/popular")
    public BooksPageDto getPopularBooksPage(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit) {
        return new BooksPageDto(booksRatingAndPopularityService.getPageOfPopularBooks(offset, limit).getContent());
    }

    @GetMapping("/search/page/{searchWord}")
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto) {
        return new BooksPageDto(bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit).getContent());
    }

    @GetMapping("/api/books/tag/{tagId}")
    public BooksPageDto getBooksByTagId(@PathVariable(value = "tagId") Integer tagId,
                                        @RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit) {
        return new BooksPageDto(tagService.getBooksByTagId(tagId, offset, limit).getContent());
    }

    @GetMapping("/api/books/genre/{genreId}")
    public BooksPageDto getBooksByGenre(@PathVariable(value = "genreId") Integer genreId,
                                        @RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit) {
        return new BooksPageDto(genresService.getBooksByGenreId(genreId, offset, limit).getContent());
    }

    @ApiOperation("method to get map of authors")
    @GetMapping("/api/authors")
    public Map<String, List<Author>> authors(){
        return authorService.getAuthorsMap();
    }

    @GetMapping("/api/books/author/{authorId}")
    public BooksPageDto getBooksByAuthor(@PathVariable("authorId") Integer authorId,
                                         @RequestParam("offset") Integer offset,
                                         @RequestParam("limit") Integer limit) {
        return new BooksPageDto(authorService.getBooksByAuthorId(authorId, offset, limit).getContent());
    }

    @PostMapping("/api/changeBookStatus")
    public ResponseEntity<ChangeBookStatusDto> changeBookStatus(@RequestParam(value = "booksIds[]", required = false) String[] booksIds,
                                                                @RequestParam(value = "status", required = false) String status) {
        return new ResponseEntity<>(changeBookStatusService.changeBookStatus(booksIds, status), HttpStatus.OK);
    }

    @PostMapping("/api/bookReview")
    public ResponseEntity<BookReviewDto> bookReviewDto(HttpServletRequest request) {
        return new ResponseEntity<BookReviewDto>(reviewService.review(request), HttpStatus.OK);
    }

    @PostMapping("/api/rateBookReview")
    public ResponseEntity<BookReviewLikeDto> bookReviewLike(HttpServletRequest request) {
        return new ResponseEntity<BookReviewLikeDto>(reviewService.like(request), HttpStatus.OK);
    }

    @PostMapping("/api/payment")
    public ResponseEntity<Payment> handlePayment(@RequestParam(value = "hash") String hash,
                                      @RequestParam(value = "sum") String sum,
                                      @RequestParam(value = "time") String time) {
        Payment payment = paymentService.generatePayment(sum);
        return new ResponseEntity<>(paymentService.createPayment(payment, hash, time), HttpStatus.OK);
    }

    @PostMapping("/api/order")
    public ResponseEntity<?> handleOrder(@RequestParam(value = "booksIds") String[] booksIds,
                                         @RequestParam(value = "sum") String sum) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals("anonymousUser")) {
            return new ResponseEntity<>(new RedirectDto("/signin"), HttpStatus.OK);
        }
        return new ResponseEntity<>(orderService.makeOrder(booksIds, sum), HttpStatus.OK);
    }
}
