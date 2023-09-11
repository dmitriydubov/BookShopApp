package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByBookId(Integer id);

    List<Rating> findByRatingAndBookId(Integer ratingValue, Integer bookId);
}
