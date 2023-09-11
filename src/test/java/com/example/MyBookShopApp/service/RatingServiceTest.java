package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.BookReview;
import com.example.MyBookShopApp.model.BookReviewLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RatingServiceTest {
    private BookReview review;
    private final RatingService ratingService;

    @Autowired
    RatingServiceTest(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @BeforeEach
    void setUp() {
        review = new BookReview();
        BookReviewLike firstLike = new BookReviewLike();
        firstLike.setValue(1);
        BookReviewLike secondLike = new BookReviewLike();
        secondLike.setValue(1);
        BookReviewLike firstDislike = new BookReviewLike();
        firstDislike.setValue(-1);
        List<BookReviewLike> likes = List.of(firstLike, secondLike, firstDislike);
        review.setLikes(likes);
    }

    @Test
    void setReviewRating() {
        ratingService.setReviewRating(review);
        int result = (int) review.getRatingStars().stream().filter(ratingStar -> ratingStar.equals("filledStar")).count();
        assertEquals(4, result);
    }
}