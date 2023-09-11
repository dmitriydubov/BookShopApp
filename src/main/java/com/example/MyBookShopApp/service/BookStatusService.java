package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.SuccessfulChangeBookStatusDto;
import com.example.MyBookShopApp.dtoAbstract.ChangeBookStatusDto;
import com.example.MyBookShopApp.model.Book2User;
import com.example.MyBookShopApp.repository.Book2UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookStatusService {
    private final Book2UserRepository book2UserRepository;

    @Autowired
    public BookStatusService(Book2UserRepository book2UserRepository) {
        this.book2UserRepository = book2UserRepository;
    }

    public ChangeBookStatusDto changeBookStatus() {
        return new SuccessfulChangeBookStatusDto(true);
    }
}
