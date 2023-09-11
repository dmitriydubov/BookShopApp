package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.ContactType;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.model.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserContactRepository extends JpaRepository<UserContact, Integer> {
    UserContact findByUserAndType(User user, ContactType email);

    Optional<UserContact> findByContact(String email);

}
