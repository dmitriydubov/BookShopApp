package com.example.MyBookShopApp.security.service;

import com.example.MyBookShopApp.errors.exceptions.NonUniqueBookStoreUserException;
import com.example.MyBookShopApp.errors.exceptions.RegistrationFormEmptyFieldException;
import com.example.MyBookShopApp.model.ContactType;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.model.UserContact;
import com.example.MyBookShopApp.repository.UserContactRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.security.dto.*;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.security.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailService;
    private final JWTUtil jwtUtil;
    private final VerificationTokenService verificationTokenService;
    private final TokenBlackListService tokenBlackListService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //==================================================================================================================
    private static final String HOST = "http://localhost:8085";
    private static final String VERIFICATION_LINK = HOST + "/confirmUserDataChange";
    private static final String IS_NOT_REGISTERED_USER = "Данный пользователь не зарегистрирован";
    private static final String SUCCESSFUL_PASSWORD_CHANGE = "Пароль успешно изменён";
    private static final String WRONG_EMAIL_OR_PASSWORD = "Неверный логин или пароль";

    @Value("${appEmail.email}")
    private String appEmail;

    @Autowired
    public AuthService(UserRepository userRepository,
                       UserContactRepository userContactRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserDetailService userDetailService,
                       JWTUtil jwtUtil,
                       VerificationTokenService verificationTokenService,
                       TokenBlackListService tokenBlackListService) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailService = userDetailService;
        this.jwtUtil = jwtUtil;
        this.verificationTokenService = verificationTokenService;
        this.tokenBlackListService = tokenBlackListService;
    }

    @Transactional
    public ContactConfirmationDto getContactConfirmationResponse(String contact) {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String referer = ra != null ?
            ra.getRequest().getHeader("Referer").replaceAll(HOST, "").trim() :
            "";
        ContactConfirmationDto confirmationDto;
        switch (referer) {
            case "/signin", "/recovery", "/confirmUserDataChange" -> confirmationDto = new SuccessfulContactConfirmation(true);
            case "/signup", "/changePassword", "/reg" -> {
                VerificationToken verificationToken = new VerificationToken();
                verificationToken.setToken(verificationTokenService.generateToken());
                verificationToken.setContact(contact);
                new Thread(() -> verificationTokenService.sendTokenToEmail(contact, verificationToken.getToken())).start();
                verificationTokenService.saveVerificationToken(verificationToken);
                confirmationDto = new SuccessfulContactConfirmation(true);
            }
            default -> {
                confirmationDto = new SuccessfulContactConfirmation(true);
            }
        }
        return confirmationDto;
    }

    public ApproveDto getContactApprove(String contact, String code) {
        Optional<VerificationToken> optionalVerificationToken = verificationTokenService.getVerificationToken(contact, code);
        if (optionalVerificationToken.isEmpty()) {
            return new ErrorApproveDto(false, "Введен неверный код");
        }
        return new SuccessfulApproveDto(true);
    }

    @Transactional
    public String changeUserPassword(RecoveryForm recoveryForm) {
        Optional<User> optionalUser = userRepository.findByContact(recoveryForm.getEmail());
        if (optionalUser.isEmpty()) return IS_NOT_REGISTERED_USER;
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(recoveryForm.getPassword()));
        userRepository.save(user);
        return SUCCESSFUL_PASSWORD_CHANGE;
    }

    public UserChangeDataDto handleUserChangeData(String userName, String userEmail, UserDataForm userChangeData) {
        String dataMethod = getUserDataChangeMethod(userName, userEmail, userChangeData);
        logger.info("user data change method is " + dataMethod);
        switch (dataMethod) {
            case "NO_CHANGE_DETECTED" -> {
                return new UserChangeDataDto(false, "Укажите данные для изменения");
            }
            case "PASSWORDS_DID_NOT_MATCH" -> {
                return new UserChangeDataDto(false, "Пароли не совпадают");
            }
            case "EMPTY_PASSWORD_FIELD" -> {
                return new UserChangeDataDto(false, "Поля смены пароля не могут быть пустыми");
            }
            case "EMAIL_ASSOCIATED_WITH_OTHER_ACCOUNT" -> {
                return new UserChangeDataDto(false, "Пользователь с указанным email уже зарегистрирован");
            }
            case "CHANGE_USER_DATA" -> {
                String verificationToken = verificationTokenService.generateTokenForVerificationLink();
                new Thread(() -> verificationTokenService.sendVerificationLinkToEmail(
                        userEmail, VERIFICATION_LINK, verificationToken, userChangeData)
                ).start();

                return new UserChangeDataDto(
                    true,
                    "Для подтверждения смены учётных данных перейдите по ссылке, отправленной на Вашу почту."
                );
            }
        }

        return new UserChangeDataDto(false, "Error!");
    }

    private String getUserDataChangeMethod(String userName, String userEmail, UserDataForm userChangeData) {
        boolean isNoUserDataChanges = checkIsUserDataChanges(userName, userEmail, userChangeData);
        boolean isUserPasswordChange = checkIsUserPasswordChanges(userChangeData);
        boolean isUserPasswordHasEmptyFields = checkIsUserPasswordHasEmptyFields(userChangeData);
        boolean isAlreadyExistAccountWithThisEmail = checkIsChangeEmailAlreadyExistWithOtherAccount(
                userEmail, userChangeData.getMail()
        );

        if (isNoUserDataChanges) {
            return "NO_CHANGE_DETECTED";
        }
        if (isUserPasswordChange && !userChangeData.getPassword().equals(userChangeData.getPasswordReply())) {
            return "PASSWORDS_DID_NOT_MATCH";
        }
        if (isUserPasswordHasEmptyFields) {
            return "EMPTY_PASSWORD_FIELD";
        }
        if (isAlreadyExistAccountWithThisEmail) {
            return "EMAIL_ASSOCIATED_WITH_OTHER_ACCOUNT";
        }

        return "CHANGE_USER_DATA";
    }

    private boolean checkIsUserDataChanges(String userName, String userEmail, UserDataForm userChangeData) {
        return userName.equals(userChangeData.getName()) &&
               userEmail.equals(userChangeData.getMail()) &&
               userChangeData.getPassword().equals("") &&
               userChangeData.getPasswordReply().equals("");
    }

    private boolean checkIsUserPasswordChanges(UserDataForm userChangeData) {
        return !userChangeData.getPassword().equals("") &&
               !userChangeData.getPasswordReply().equals("");
    }

    private boolean checkIsUserPasswordHasEmptyFields(UserDataForm userChangeData) {
        return userChangeData.getPassword().equals("") && !userChangeData.getPasswordReply().equals("") ||
               !userChangeData.getPassword().equals("") && userChangeData.getPasswordReply().equals("");
    }

    private boolean checkIsChangeEmailAlreadyExistWithOtherAccount(String userEmail, String emailFromUserChangeData) {
        if (userEmail.equals(emailFromUserChangeData)) return false;
        Optional<UserContact> userContact = userContactRepository.findByContact(emailFromUserChangeData);
        return userContact.isPresent() || emailFromUserChangeData.equals(appEmail);
    }

    @Transactional
    public void changeUserData(ConfirmationUserChangeData userChangeData, User user, String password) {
        try {
            UserContact userContact = userContactRepository.findByContact(userChangeData.getPreviousEmail()).orElseThrow();
            userContact.setContact(userChangeData.getEmail());
            user.setName(userChangeData.getName());
            if (password != null) {
                user.setPassword(password);
            }
            userRepository.saveAndFlush(user);
            userContact.setUser(user);
            userContactRepository.save(userContact);

            removeAuthentication();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Transactional
    public Boolean registerUser(RegistrationForm registrationForm) throws RegistrationFormEmptyFieldException, NonUniqueBookStoreUserException {
        checkRegistration(registrationForm);

        User user = new User();
        user.setName(registrationForm.getName());
        user.setHash(UUID.randomUUID().toString());
        user.setRegTime(new Date());
        user.setPassword(passwordEncoder.encode(registrationForm.getPassword()));

        UserContact userContactEmail = new UserContact();
        userContactEmail.setContact(registrationForm.getEmail());
        userContactEmail.setUser(user);
        userContactEmail.setType(ContactType.EMAIL);
        userContactEmail.setApproved(1);

        userRepository.saveAndFlush(user);
        userContactRepository.saveAndFlush(userContactEmail);

        return true;
    }

    @Transactional
    public void registerOAuth2User(OAuth2User oAuth2User) {
        Optional<UserContact> optionalUserContact = userContactRepository.findByContact(oAuth2User.getAttribute("email"));
        if (optionalUserContact.isEmpty()) {
            User user = new User();
            user.setName(oAuth2User.getAttribute("name"));
            user.setHash(UUID.randomUUID().toString());
            user.setRegTime(new Date());
            user.setPassword(passwordEncoder.encode(oAuth2User.getName()));

            UserContact userContactEmail = new UserContact();
            userContactEmail.setContact(oAuth2User.getAttribute("email"));
            userContactEmail.setUser(user);
            userContactEmail.setType(ContactType.EMAIL);

            userRepository.saveAndFlush(user);
            userContactRepository.saveAndFlush(userContactEmail);
        }
    }

    public LoginDto login(String contact, String code) {
        Optional<User> optionalUser = userRepository.findByContact(contact);
        if (optionalUser.isEmpty()) {
            return new ErrorLogin(false, WRONG_EMAIL_OR_PASSWORD);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(contact, code)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new SuccessfulLogin(true);
        } catch (BadCredentialsException ex) {
            return new ErrorLogin(false, WRONG_EMAIL_OR_PASSWORD);
        }
    }
    public LoginDto jwtLogin(String contact, String code, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByContact(contact);

        if (optionalUser.isEmpty()) {
            return new ErrorLogin(false, WRONG_EMAIL_OR_PASSWORD);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(contact, code));
            BookstoreUserDetails userDetails =
                    (BookstoreUserDetails) userDetailService.loadUserByUsername(contact);
            String jwtToken = jwtUtil.generateToken(userDetails);
            Cookie cookie = new Cookie("token", jwtToken);
            cookie.setPath("/");
            response.addCookie(cookie);

            return new SuccessfulLogin(true);
        } catch (BadCredentialsException exception) {
            return new ErrorLogin(false, WRONG_EMAIL_OR_PASSWORD);
        }
    }

    public User getCurrentUser(Authentication authentication) {
        return userRepository.findByContact(authentication.getName()).orElseThrow();
    }


    public UserContact getUserEmail(User currentUser) {
        return userContactRepository.findByUserAndType(currentUser, ContactType.EMAIL);
    }

    public UserContact getUserPhone(User currentUser) {
        return userContactRepository.findByUserAndType(currentUser, ContactType.PHONE);
    }

    private void checkRegistration(RegistrationForm registrationForm) throws NonUniqueBookStoreUserException, RegistrationFormEmptyFieldException {
        Optional<User> optionalUserWithPhone = userRepository.findByContact(registrationForm.getPhone());
        Optional<User> optionalUserWithEmail = userRepository.findByContact(registrationForm.getEmail());
        boolean isEmptyFieldName = registrationForm.getName().trim().equals("");

        if (optionalUserWithPhone.isPresent() || optionalUserWithEmail.isPresent()) {
            throw new NonUniqueBookStoreUserException("Пользователь с указанными данными уже зарегистрирован");
        }

        if (isEmptyFieldName) {
            throw new RegistrationFormEmptyFieldException("Имя не указано. Пожалуйста, введите имя");
        }
    }

    public Boolean checkAuthentication(Authentication authentication) {
        return !authentication.getPrincipal().equals("anonymousUser");
    }

    public void removeAuthentication() throws ServletException {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request;
        if (ra != null) {
            request = ra.getRequest();
            request.logout();
        }
    }
}
