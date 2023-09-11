package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findByParentId(Integer id);

    Genre findBySlug(String slug);

    Genre findGenreById(Integer id);
}
