package com.example.MyBookShopApp.security.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "confirmation_user_change_data")
@Getter
@Setter
public class ConfirmationUserChangeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(name = "verification_token", nullable = false)
    private String verificationToken;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "previous_email", nullable = false)
    private String previousEmail;

    @Column(nullable = false)
    private Date time;
}
