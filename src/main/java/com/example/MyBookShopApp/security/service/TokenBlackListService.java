package com.example.MyBookShopApp.security.service;

import com.example.MyBookShopApp.security.model.JwtToken;
import com.example.MyBookShopApp.security.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenBlackListService {
    private final TokenRepository tokenRepositoryBlackList;

    @Autowired
    public TokenBlackListService(TokenRepository tokenRepository) {
        this.tokenRepositoryBlackList = tokenRepository;
    }

    public void setTokenToBlackList(String token) {
        JwtToken expiredJwtToken = new JwtToken();
        expiredJwtToken.setToken(token);
        tokenRepositoryBlackList.save(expiredJwtToken);
    }
}
