package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.dtoAbstract.OrderDto;
import lombok.Getter;

@Getter
public class SucceedOrderDto extends OrderDto {
    public SucceedOrderDto(Boolean result) {
        super(result);
    }
}
