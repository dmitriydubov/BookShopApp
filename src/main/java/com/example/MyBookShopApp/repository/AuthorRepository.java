package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    @Query("SELECT a FROM Author a JOIN a.book2Author b2a JOIN b2a.book b WHERE b.id = :id")
    Author findByBookId(Integer id);

    Author findBySlug(String slug);

    @Query("SELECT a FROM Author a JOIN a.book2Author b2a JOIN b2a.book b WHERE b.slug = :slug")
    Author findByBookSlug(String slug);
}
