package com.example.MyBookShopApp.security.repository;

import com.example.MyBookShopApp.security.model.ConfirmationUserChangeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationUserChangeDataRepository extends JpaRepository<ConfirmationUserChangeData, Integer> {
    Optional<ConfirmationUserChangeData> findByVerificationToken(String token);
}
