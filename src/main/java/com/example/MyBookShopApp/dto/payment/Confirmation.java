package com.example.MyBookShopApp.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Confirmation {
    private String type;

    @JsonProperty("confirmation_url")
    private String confirmationUrl;

    @JsonProperty("return_url")
    private String returnUrl;
}
