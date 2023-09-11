package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.Book2User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2User, Integer> {

    void deleteByBookId(Integer bookId);

    Optional<Book2User> findByBookId(Integer bookId);
}
