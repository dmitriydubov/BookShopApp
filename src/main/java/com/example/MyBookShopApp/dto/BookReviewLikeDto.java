package com.example.MyBookShopApp.dto;


public class BookReviewLikeDto {
    private boolean result;

    public BookReviewLikeDto(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
