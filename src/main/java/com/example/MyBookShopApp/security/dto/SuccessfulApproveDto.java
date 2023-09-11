package com.example.MyBookShopApp.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfulApproveDto extends ApproveDto{
    public SuccessfulApproveDto(Boolean result) {
        super(result);
    }
}
