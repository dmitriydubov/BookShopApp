package com.example.MyBookShopApp.model;

import com.example.MyBookShopApp.dto.payment.Payment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(name = "user_hash", nullable = false)
    private String userHash;

    @Column(nullable = false)
    private Date time;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String status;
}
