package com.example.MyBookShopApp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Component
@Configuration
public class YooKassaConfig {

    @Value("${yookassa.shopId}")
    private String shopId;

    @Value("${yookassa.access.token}")
    private String accessToken;
}
