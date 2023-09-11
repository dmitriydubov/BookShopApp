package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.BookReview;
import com.example.MyBookShopApp.model.Rating;
import com.example.MyBookShopApp.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final BookService bookService;

    @Autowired
    public RatingService(RatingRepository ratingRepository,
                         BookService bookService) {
        this.ratingRepository = ratingRepository;
        this.bookService = bookService;
    }

    public void changeBookRating(Integer bookId, Integer ratingValue) {
        saveRatingToDb(bookId, ratingValue);
    }

    public void setBookRatingStars(Book book) {
        List<Rating> ratingList = ratingRepository.findByBookId(book.getId());
        List<String> stars = new ArrayList<>();

        double rating = ratingList.size() > 0 ? ratingList.stream()
                .map(entity -> (double) entity.getRating())
                .reduce(0.0, Double::sum) / ratingList.size() : 0.0;

        int finalRating = (int) Math.round(rating);

        for (int i = 0; i < finalRating; i++) {
            stars.add("filledStar");
        }

        for (int i = 0; i < (5 - finalRating); i++) {
            stars.add("emptyStar");
        }

        book.setRatingStars(stars);
    }

    public Integer getAllRatings(Book book) {
        return ratingRepository.findByBookId(book.getId()).size();
    }

    public List<Integer> getAllRatingsByScore(Book book) {
        List<Integer> ratingListByScore = new ArrayList<>();

        for (int i = 5; i > 0; i--) {
            ratingListByScore.add(ratingRepository.findByRatingAndBookId(i, book.getId()).size());
        }

        return ratingListByScore;
    }

    @Transactional
    private void saveRatingToDb(Integer bookId, Integer ratingValue) {
        Rating rating = new Rating();
        rating.setRating(ratingValue);
        rating.setBook(bookService.getBookById(bookId));

        ratingRepository.save(rating);
    }

    public void setReviewRating(BookReview review) {
        List<String> ratingStars = new ArrayList<>();

        double rating = review.getLikes().size() > 0 ? review.getLikes()
                .stream()
                .map(entity -> (double) entity.getValue())
                .map(doubleValue -> {
                    if (doubleValue == 1.0) {
                        doubleValue = doubleValue * 5;
                    } else {
                        doubleValue = 1.0;
                    }
                    return doubleValue;
                }).reduce( 0.0, Double::sum) / review.getLikes().size() : 0.0;

        int finalRating = (int) Math.round(rating);

        for (int i = 0; i < finalRating; i++) {
            ratingStars.add("filledStar");
        }

        for (int i = 0; i < (5 - finalRating); i++) {
            ratingStars.add("emptyStar");
        }

        review.setRatingStars(new ArrayList<>(ratingStars));
    }
}
