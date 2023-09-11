package com.example.MyBookShopApp.security.service;

import com.example.MyBookShopApp.security.model.ConfirmationUserChangeData;
import com.example.MyBookShopApp.security.model.UserDataForm;
import com.example.MyBookShopApp.security.model.VerificationToken;
import com.example.MyBookShopApp.security.repository.ConfirmationUserChangeDataRepository;
import com.example.MyBookShopApp.security.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private final JavaMailSender mailSender;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ConfirmationUserChangeDataRepository userChangeDataRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${appEmail.email}")
    private String appEmail;

    @Autowired
    public VerificationTokenService(JavaMailSender mailSender,
                                    VerificationTokenRepository verificationTokenRepository,
                                    ConfirmationUserChangeDataRepository userChangeDataRepository,
                                    BCryptPasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userChangeDataRepository = userChangeDataRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateToken() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        while (sb.length() < 6) {{
            sb.append(random.nextInt(9));
        }}

        return sb.toString();
    }

    public String generateTokenForVerificationLink() {
        return UUID.randomUUID().toString();
    }

    public synchronized void sendTokenToEmail(String contact, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(appEmail);
        mailMessage.setTo(contact);
        mailMessage.setSubject("Bookstore user mail verification");
        mailMessage.setText("Varification code is: " + token);
        mailSender.send(mailMessage);
    }

    @Transactional
    public synchronized void sendVerificationLinkToEmail(String userEmail,
                                                         String link,
                                                         String token,
                                                         UserDataForm userChangeData) {
        ConfirmationUserChangeData confirmationUserChangeData = new ConfirmationUserChangeData();
        confirmationUserChangeData.setVerificationToken(token);
        confirmationUserChangeData.setName(userChangeData.getName());
        confirmationUserChangeData.setEmail(userChangeData.getMail());
        confirmationUserChangeData.setTime(new Date());
        confirmationUserChangeData.setPreviousEmail(userEmail);
        userChangeDataRepository.saveAndFlush(confirmationUserChangeData);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(appEmail);
        mailMessage.setTo(userChangeData.getMail());
        if (!userChangeData.getPassword().equals("") && !userChangeData.getPasswordReply().equals("")) {
            mailMessage.setSubject("Подтверждение изменения пароля");
            mailMessage.setText(
                "Перейдите по ссылке: " +
                link +
                "?token=" + token +
                "&&password=" + passwordEncoder.encode(userChangeData.getPassword())
            );
        } else {
            mailMessage.setSubject("Подтверждение изменения учётных данных");
            mailMessage.setText(
                "Перейдите по ссылке: " +
                link +
                "?token=" + token
            );
        }
        mailSender.send(mailMessage);
    }


    @Transactional
    public void saveVerificationToken(VerificationToken token) {
        verificationTokenRepository.save(token);
    }

    public Optional<VerificationToken> getVerificationToken(String contact, String code) {
        return verificationTokenRepository.findByContactAndToken(contact, code.replaceAll(" ", "").trim());
    }
}
