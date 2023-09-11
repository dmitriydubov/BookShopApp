package com.example.MyBookShopApp.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class ContactConfirmationDto {
    private Boolean result;
}
