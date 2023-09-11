package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CartController {
    private final CookieService cookieService;
    private final PriceService priceService;
    private final BookService bookService;

    @Autowired
    public CartController(CookieService cookieService,
                          PriceService priceService,
                          BookService bookService) {
        this.cookieService = cookieService;
        this.priceService = priceService;
        this.bookService = bookService;
    }

    @ModelAttribute(name = "cartBooks")
    public List<Book> cartBooks() {return new ArrayList<>();}

    @GetMapping("/cart")
    public String handleCart(@CookieValue(value = "cartContents", required = false) String cartContents, Model model) {
        try {
            if (cartContents != null && !cartContents.equals("")) {
                List<Book> booksFromBooksSlug = cookieService.getListOfBookSlugFromCookie(cartContents, cartBooks());
                model.addAttribute("cartBooks", booksFromBooksSlug);
                model.addAttribute("totalPrice", priceService.getTotalPriceWithoutDiscount(booksFromBooksSlug));
                model.addAttribute("totalPriceWithDiscount", priceService.getTotalPriceWithDiscount(booksFromBooksSlug));
                model.addAttribute("booksIds", bookService.generateBooksIdsStringForTagAttribute(booksFromBooksSlug));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "/cart";
    }
}
