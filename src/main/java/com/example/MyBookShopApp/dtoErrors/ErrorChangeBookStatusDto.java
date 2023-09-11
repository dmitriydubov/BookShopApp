package com.example.MyBookShopApp.dtoErrors;

import com.example.MyBookShopApp.dtoAbstract.ChangeBookStatusDto;

public class ErrorChangeBookStatusDto extends ChangeBookStatusDto {
    private Boolean result;
    private String error;

    public ErrorChangeBookStatusDto(Boolean result, String error) {
        super(result);
        this.error = error;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
