package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Tag findByTag(String tagName);

    Optional<Tag> findById(Integer tagId);
}
