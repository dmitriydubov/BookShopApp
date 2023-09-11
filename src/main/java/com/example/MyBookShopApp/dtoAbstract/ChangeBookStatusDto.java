package com.example.MyBookShopApp.dtoAbstract;
public abstract class ChangeBookStatusDto {
    private Boolean result;

    public ChangeBookStatusDto(Boolean result) {
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
