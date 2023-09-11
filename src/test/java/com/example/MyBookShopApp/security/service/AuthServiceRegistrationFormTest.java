package com.example.MyBookShopApp.security.service;

import com.example.MyBookShopApp.errors.exceptions.NonUniqueBookStoreUserException;
import com.example.MyBookShopApp.errors.exceptions.RegistrationFormEmptyFieldException;
import com.example.MyBookShopApp.security.model.RegistrationForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthServiceRegistrationFormTest {
    private String contact;
    private String password;
    private final AuthService authService;

    @Autowired
    public AuthServiceRegistrationFormTest(AuthService authService) {
        this.authService = authService;
    }

    @BeforeEach
    void setUp() {
        contact = "test2@mail.ru";
        password = "1234567";
    }

    @Test
    @Transactional
    void registerUser() throws RegistrationFormEmptyFieldException, NonUniqueBookStoreUserException {
        RegistrationForm registrationForm = new RegistrationForm();

        registrationForm.setName("Test2");
        registrationForm.setPhone("+7 (322) 222-22-22");
        registrationForm.setPassword(password);
        registrationForm.setEmail(contact);

        boolean isRegisteredUser = authService.registerUser(registrationForm);
        assertTrue(isRegisteredUser);
    }
}
