package com.example.MyBookShopApp.security.repository;

import com.example.MyBookShopApp.security.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {

    Optional<VerificationToken> findByContactAndToken(String contact, String code);
}
