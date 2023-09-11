package com.example.MyBookShopApp.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfulLogin extends LoginDto {
    public SuccessfulLogin(Boolean result) {
        super(result);
    }
}
