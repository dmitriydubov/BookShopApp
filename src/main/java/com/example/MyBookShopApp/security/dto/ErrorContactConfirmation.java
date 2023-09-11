package com.example.MyBookShopApp.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorContactConfirmation extends ContactConfirmationDto {
    private String error;

    public ErrorContactConfirmation(Boolean result, String error) {
        super(result);
        this.error = error;
    }
}
