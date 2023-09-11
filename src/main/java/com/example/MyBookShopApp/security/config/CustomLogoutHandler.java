package com.example.MyBookShopApp.security.config;

import com.example.MyBookShopApp.security.service.TokenBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenBlackListService tokenBlackListService;

    @Autowired
    public CustomLogoutHandler(TokenBlackListService tokenBlackListService) {
        this.tokenBlackListService = tokenBlackListService;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        Optional<String> optionalToken = Stream.of(cookies)
                .filter(cookie -> cookie.getName().equals("token"))
                .map(Cookie::getValue)
                .findFirst();


        optionalToken.ifPresent(tokenBlackListService::setTokenToBlackList);
    }
}
