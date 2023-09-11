package com.example.MyBookShopApp.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorApproveDto extends ApproveDto {
    private String error;

    public ErrorApproveDto(Boolean result, String error) {
        super(result);
        this.error = error;
    }
}
