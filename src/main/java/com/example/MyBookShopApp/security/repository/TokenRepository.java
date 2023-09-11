package com.example.MyBookShopApp.security.repository;

import com.example.MyBookShopApp.security.model.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<JwtToken, Integer> {
    Optional<JwtToken> findByToken(String token);
}
