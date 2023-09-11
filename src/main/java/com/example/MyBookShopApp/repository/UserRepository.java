package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByHash(String hash);

    @Query("SELECT u FROM User u JOIN u.userContacts uc WHERE uc.contact = :contact")
    Optional<User> findByContact(String contact);


}
