package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.BookReview;
import com.example.MyBookShopApp.model.BookReviewLike;
import com.example.MyBookShopApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookReviewLikeRepository extends JpaRepository<BookReviewLike, Integer> {
    @Modifying
    @Query("DELETE FROM BookReviewLike WHERE bookReview = :bookReview AND user = :user AND value = :value")
    void deleteLastLikeByBookReviewAndUser(BookReview bookReview, User user, Integer value);

    @Modifying
    @Query("DELETE FROM BookReviewLike " +
           "WHERE id = (SELECT max(id) " +
           "FROM BookReviewLike) AND bookReview = :bookReview AND user = :user")
    void deleteLastLikeByBookReviewAndUser(BookReview bookReview, User user);

    @Query("SELECT brl FROM BookReviewLike brl " +
           "WHERE brl.bookReview = :bookReview " +
           "AND brl.user = :user ORDER BY brl.time DESC")
    Optional<BookReviewLike> findLastUserLike(BookReview bookReview, User user);
}
