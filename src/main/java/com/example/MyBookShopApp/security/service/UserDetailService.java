package com.example.MyBookShopApp.security.service;

import com.example.MyBookShopApp.model.ContactType;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.model.UserContact;
import com.example.MyBookShopApp.repository.UserContactRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.security.model.BookstoreUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserContactRepository userContactRepository;

    @Autowired
    public UserDetailService(UserRepository userRepository, UserContactRepository userContactRepository) {
        this.userRepository = userRepository;
        this.userContactRepository = userContactRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByContact(s);

        if (optionalUser.isPresent()) {
            UserContact contact = userContactRepository.findByUserAndType(optionalUser.get(), ContactType.EMAIL);
            return new BookstoreUserDetails(optionalUser.get(), contact);
        } else {
            throw new UsernameNotFoundException("user not found");
        }
    }
}
