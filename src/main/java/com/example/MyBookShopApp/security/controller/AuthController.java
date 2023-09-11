package com.example.MyBookShopApp.security.controller;

import com.example.MyBookShopApp.errors.exceptions.NonUniqueBookStoreUserException;
import com.example.MyBookShopApp.errors.exceptions.RegistrationFormEmptyFieldException;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.security.model.ConfirmationUserChangeData;
import com.example.MyBookShopApp.security.model.RecoveryForm;
import com.example.MyBookShopApp.security.model.RegistrationForm;
import com.example.MyBookShopApp.security.model.UserDataForm;
import com.example.MyBookShopApp.security.repository.ConfirmationUserChangeDataRepository;
import com.example.MyBookShopApp.security.service.AuthService;
import com.example.MyBookShopApp.service.BalanceTransactionService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AuthController {
    private final AuthService authService;
    private final PaymentService paymentService;
    private final BalanceTransactionService balanceTransactionService;
    private final BookService bookService;
    private final UserRepository userRepository;
    private final ConfirmationUserChangeDataRepository userChangeDataRepository;

    @Autowired
    public AuthController(AuthService authService,
                          PaymentService paymentService,
                          BalanceTransactionService balanceTransactionService,
                          BookService bookService,
                          UserRepository userRepository,
                          ConfirmationUserChangeDataRepository userChangeDataRepository) {
        this.authService = authService;
        this.paymentService = paymentService;
        this.balanceTransactionService = balanceTransactionService;
        this.bookService = bookService;
        this.userRepository = userRepository;
        this.userChangeDataRepository = userChangeDataRepository;
    }

    @GetMapping("/signin")
    public String handleSignin(Principal user) {
        if (user == null) {
            return "signin";
        } else {
            return "redirect:/my";
        }
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/reg")
    public String reg(RegistrationForm registrationForm, Model model) throws RegistrationFormEmptyFieldException, NonUniqueBookStoreUserException {
        model.addAttribute("regOk", authService.registerUser(registrationForm));
        return "signin";
    }

    @PostMapping("/recovery")
    public String recovery(RecoveryForm recoveryForm, Model model) {
        model.addAttribute("responseMessage", "Пароль успешно изменен!");
        model.addAttribute("recoveryResult", authService.changeUserPassword(recoveryForm));
        return "signin";
    }

    @GetMapping("/my")
    public String myPage(Authentication authentication, Model model) {
        try {
            User currentUser = authService.getCurrentUser(authentication);
            model.addAttribute("user", currentUser);
            model.addAttribute("userBooks", bookService.getUserBooks(currentUser));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "my";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User currentUser = authService.getCurrentUser(authentication);
        String userEmail = authService.getUserEmail(currentUser).getContact();
        paymentService.updateUserTransactions(currentUser);
        model.addAttribute("user", currentUser);
        model.addAttribute("userTransactions", balanceTransactionService.getUserTransactions(currentUser));
        model.addAttribute("userEmail", userEmail);
        model.addAttribute(
                "userDataForm",
                new UserDataForm(){
                    {
                        setName(currentUser.getName());
                        setMail(userEmail);
                    }
                }
        );
        return "profile";
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("recoveryForm", new RecoveryForm());
        return "recovery";
    }

    @GetMapping("/confirmUserDataChange")
    public String handleUserDataChangeConfirmation(@RequestParam("token") String token,
                                                   @RequestParam(value = "password", required = false) String password,
                                                   RedirectAttributes ra) {
        Optional<ConfirmationUserChangeData> optionalUserChangeData = userChangeDataRepository.findByVerificationToken(token);
        String redirectUrl = "signin";
        if (optionalUserChangeData.isEmpty()) {
            throw new RuntimeException();
        }

        Optional<User> optionalUser = userRepository.findByContact(optionalUserChangeData.get().getPreviousEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException();
        }

        authService.changeUserData(optionalUserChangeData.get(), optionalUser.get(), password);
        ra.addFlashAttribute("responseMessage", "Учётные данные изменены");

        return "redirect:" + redirectUrl;
    }
}
