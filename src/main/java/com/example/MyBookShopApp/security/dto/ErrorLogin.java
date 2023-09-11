package com.example.MyBookShopApp.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogin extends LoginDto {
    private String error;
    public ErrorLogin(Boolean result, String error) {
        super(result);
        this.error = error;
    }
}
