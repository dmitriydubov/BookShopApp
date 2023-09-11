package com.example.MyBookShopApp.dtoErrors;

import com.example.MyBookShopApp.dtoAbstract.BookReviewDto;

public class ErrorBookReviewDto extends BookReviewDto {
    private String error;

    public ErrorBookReviewDto(Boolean result, String error) {
        super(result);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
