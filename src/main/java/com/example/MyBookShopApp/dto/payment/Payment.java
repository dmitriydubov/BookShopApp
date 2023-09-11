package com.example.MyBookShopApp.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
    private String id;
    private String status;
    private Amount amount;
    private Recipient recipient;

    @JsonProperty("created_at")
    private String createdAt;

    private Confirmation confirmation;
    private Boolean test;
    private Boolean paid;
    private Boolean capture;
    private Boolean refundable;
}
