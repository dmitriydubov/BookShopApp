package com.example.MyBookShopApp.security.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {
    private String name;
    private String phone;
    private String email;
    private String password;
}
