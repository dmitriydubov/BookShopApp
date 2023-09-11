package com.example.MyBookShopApp.errors.exceptions;

public class NonUniqueBookStoreUserException extends Exception {
    public NonUniqueBookStoreUserException(String message) {
        super(message);
    }
}
