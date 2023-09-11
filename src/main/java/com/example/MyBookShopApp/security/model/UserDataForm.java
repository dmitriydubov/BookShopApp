package com.example.MyBookShopApp.security.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDataForm {
    private String name;
    private String mail;
    private String password;
    private String passwordReply;
}
