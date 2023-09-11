package com.example.MyBookShopApp.dtoAbstract;

public abstract class BookReviewDto {
    private Boolean result;

    public BookReviewDto(Boolean result) {
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
