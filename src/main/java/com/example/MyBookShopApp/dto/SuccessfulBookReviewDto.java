package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.dtoAbstract.BookReviewDto;

public class SuccessfulBookReviewDto extends BookReviewDto {
    public SuccessfulBookReviewDto(Boolean result) {
        super(result);
    }
}
