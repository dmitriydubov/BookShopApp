package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewLikeDto;
import com.example.MyBookShopApp.dto.SuccessfulBookReviewDto;
import com.example.MyBookShopApp.dtoAbstract.BookReviewDto;
import com.example.MyBookShopApp.dtoErrors.ErrorBookReviewDto;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.BookReview;
import com.example.MyBookShopApp.model.BookReviewLike;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.BookReviewLikeRepository;
import com.example.MyBookShopApp.repository.BookReviewRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class ReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final AuthService authService;
    private final RatingService ratingService;

    @Autowired
    public ReviewService(BookReviewRepository bookReviewRepository,
                         BookService bookService,
                         UserRepository userRepository,
                         BookReviewLikeRepository bookReviewLikeRepository,
                         AuthService authService,
                         RatingService ratingService) {
        this.bookReviewRepository = bookReviewRepository;
        this.bookService = bookService;
        this.userRepository = userRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.authService = authService;
        this.ratingService = ratingService;
    }

    public BookReviewDto review(HttpServletRequest request) {
        Integer bookId = Integer.parseInt(request.getParameter("bookId"));
        String text = request.getParameter("text");

        if (text.length() < 2) {
            String errorMessage = "Отзыв слишком короткий. Напишите, пожалуйста более развёрнутый отзыв";
            return new ErrorBookReviewDto(false, errorMessage);
        }

        BookReview bookReview = new BookReview();
        User user = authService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());

        bookReview.setBook(bookService.getBookById(bookId));
        bookReview.setText(text);
        bookReview.setTime(new Date());
        bookReview.setUser(user);

        bookReviewRepository.save(bookReview);

        return new SuccessfulBookReviewDto(true);
    }

    public List<BookReview> getAllReviewsByBook(Book book) {
        List<BookReview> reviewList = bookReviewRepository.findAllByBook(book);
        reviewList.forEach(ratingService::setReviewRating);

        return reviewList;
    }

    public Map<String, Integer> getReviewSizeValue(Book book) {
        List<BookReview> bookReview = bookReviewRepository.findAllByBook(book);
        String message;
        int number = bookReview.size() - (bookReview.size() / 10 * 10);
        int numberException = bookReview.size() - (bookReview.size() / 100 * 100);

        if ((number == 1) && (numberException != 11)) {
            message = "Отзыв";
        } else if ((number > 1 && number < 5) && (numberException < 12 || numberException > 14)) {
            message = "Отзыва";
        } else {
            message = "Отзывов";
        }

        return new HashMap<>() {{
            put(message.trim(), bookReview.size());
        }};
    }

    @Transactional
    public BookReviewLikeDto like(HttpServletRequest request) {
        int reviewId = Integer.parseInt(request.getParameter("reviewid"));
        int value = Integer.parseInt(request.getParameter("value"));
        BookReview currentReview = bookReviewRepository.findById(reviewId).orElseThrow();
        User currentUser = authService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());

        switch (value) {
            case 1 -> addNewLike(currentReview, currentUser);
            case -1 -> addNewDislike(currentReview, currentUser);
            case 0 -> removeReview(currentReview, currentUser);
        }

        return new BookReviewLikeDto(true);
    }

    private void addNewLike(BookReview currentReview, User currentUser) {
        Integer reviewValue = 1;
        BookReviewLike like = createBookReviewLike(reviewValue, currentReview, currentUser);

        Optional<BookReviewLike> optionalPreviousUserLike =
            bookReviewLikeRepository.findLastUserLike(currentReview, currentUser);

        optionalPreviousUserLike.ifPresent(reviewLike -> {
            if (reviewLike.getValue() == -1) {
                bookReviewLikeRepository.deleteLastLikeByBookReviewAndUser(currentReview, currentUser, -1);
                bookReviewLikeRepository.save(like);
            }
        });

        if (optionalPreviousUserLike.isEmpty()) bookReviewLikeRepository.save(like);
    }

    private void addNewDislike(BookReview currentReview, User currentUser) {
        Integer reviewValue = -1;
        BookReviewLike dislike = createBookReviewLike(reviewValue, currentReview, currentUser);

        Optional<BookReviewLike> optionalPreviousUserLike =
                bookReviewLikeRepository.findLastUserLike(currentReview, currentUser);

        optionalPreviousUserLike.ifPresent(reviewLike -> {
            if (reviewLike.getValue() == 1) {
                bookReviewLikeRepository.deleteLastLikeByBookReviewAndUser(currentReview, currentUser, 1);
                bookReviewLikeRepository.save(dislike);
            }
        });

        if (optionalPreviousUserLike.isEmpty()) bookReviewLikeRepository.save(dislike);
    }

    private void removeReview(BookReview currentReview, User currentUser) {
        Optional<BookReviewLike> optionalPreviousUserLike =
                bookReviewLikeRepository.findLastUserLike(currentReview, currentUser);
        if (optionalPreviousUserLike.isEmpty()) {
            bookReviewLikeRepository.deleteLastLikeByBookReviewAndUser(currentReview, currentUser);
        }
    }

    private BookReviewLike createBookReviewLike(Integer reviewValue, BookReview currentReview, User currentUser) {
        BookReviewLike bookReviewLike = new BookReviewLike();

        bookReviewLike.setBookReview(currentReview);
        bookReviewLike.setValue(reviewValue);
        bookReviewLike.setTime(new Date());
        bookReviewLike.setUser(currentUser);

        return bookReviewLike;
    }
}
