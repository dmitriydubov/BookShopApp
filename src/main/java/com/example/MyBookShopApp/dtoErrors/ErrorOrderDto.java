package com.example.MyBookShopApp.dtoErrors;

import com.example.MyBookShopApp.dtoAbstract.OrderDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorOrderDto extends OrderDto {
    private String error;
    public ErrorOrderDto(Boolean result, String error) {
        super(result);
        this.error = error;
    }
}
