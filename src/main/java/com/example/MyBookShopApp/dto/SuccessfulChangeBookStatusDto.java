package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.dtoAbstract.ChangeBookStatusDto;

public class SuccessfulChangeBookStatusDto extends ChangeBookStatusDto {
    public SuccessfulChangeBookStatusDto(Boolean result) {
        super(result);
    }
}
