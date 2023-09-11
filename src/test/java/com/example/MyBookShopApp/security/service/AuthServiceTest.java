package com.example.MyBookShopApp.security.service;
import com.example.MyBookShopApp.security.dto.LoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AuthServiceTest {
    private String contact;
    private String password;

    private final AuthService authService;

    @Mock
    private HttpServletResponse response;

    @Autowired
    public AuthServiceTest(AuthService authService) {
        this.authService = authService;
    }

    @BeforeEach
    void setUp() {
        contact = "test@mail.ru";
        password = "1234567";
    }

    @Test
    void jwtLoginSuccessful() {
        LoginDto loginDto = authService.jwtLogin(contact, password, response);

        assertTrue(loginDto.getResult());
    }

    @Test
    void jwtLoginFailure() {
        contact = "another@mail.ru";
        password = "another_password";
        LoginDto loginDto = authService.jwtLogin(contact, password, response);

        assertFalse(loginDto.getResult());
    }
}