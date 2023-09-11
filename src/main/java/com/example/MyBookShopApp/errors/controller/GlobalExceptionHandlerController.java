package com.example.MyBookShopApp.errors.controller;
import com.example.MyBookShopApp.errors.exceptions.NonUniqueBookStoreUserException;
import com.example.MyBookShopApp.errors.exceptions.RegistrationFormEmptyFieldException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(RegistrationFormEmptyFieldException.class)
    public String handleRegistrationFormEmptyFieldException(RegistrationFormEmptyFieldException ex,
                                                            RedirectAttributes redirectAttributes) {
        return  "redirect:/signup";
    }

    @ExceptionHandler(NonUniqueBookStoreUserException.class)
    public String handleNonUniqueBookStoreUserException(NonUniqueBookStoreUserException ex,
                                                        RedirectAttributes redirectAttributes) {
        return "redirect:/signup";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException ex,
                                                  RedirectAttributes redirectAttributes) {
        return "redirect:/signin";
    }

//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public String handleWrongHttpMethod() {
//        return "redirect:/";
//    }
}
