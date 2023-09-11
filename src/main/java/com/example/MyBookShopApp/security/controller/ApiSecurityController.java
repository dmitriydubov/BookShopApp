package com.example.MyBookShopApp.security.controller;

import com.example.MyBookShopApp.errors.exceptions.NonUniqueBookStoreUserException;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.security.dto.*;
import com.example.MyBookShopApp.security.model.ConfirmationUserChangeData;
import com.example.MyBookShopApp.security.model.UserDataForm;
import com.example.MyBookShopApp.security.repository.ConfirmationUserChangeDataRepository;
import com.example.MyBookShopApp.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
public class ApiSecurityController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final ConfirmationUserChangeDataRepository userChangeDataRepository;

    @Autowired
    public ApiSecurityController(AuthService authService,
                                 UserRepository userRepository,
                                 ConfirmationUserChangeDataRepository userChangeDataRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.userChangeDataRepository = userChangeDataRepository;
    }

    @PostMapping("/requestContactConfirmation")
    public ResponseEntity<?> contactConfirmation(@RequestParam("contact") String contact) throws NonUniqueBookStoreUserException {
        return new ResponseEntity<>(authService.getContactConfirmationResponse(contact), HttpStatus.OK);
    }

    @PostMapping("/approveContact")
    public ResponseEntity<ApproveDto> approveContact(@RequestParam("contact") String contact,
                                                     @RequestParam("code") String code) {
        return new ResponseEntity<>(authService.getContactApprove(contact, code), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestParam("contact") String contact,
                                          @RequestParam("code") String code,
                                          HttpServletResponse response) {
        LoginDto loginDto = authService.jwtLogin(contact, code, response);

        return new ResponseEntity<>(loginDto, HttpStatus.OK);
    }

    @PostMapping("/changeUserData/{userName}/contact/{userEmail}")
    public ResponseEntity<UserChangeDataDto> changeUserData(@PathVariable String userName,
                                                            @PathVariable String userEmail,
                                                            UserDataForm userChangeData) {
        return new ResponseEntity<>(authService.handleUserChangeData(userName, userEmail, userChangeData), HttpStatus.OK);
    }
}
