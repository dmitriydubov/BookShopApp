package com.example.MyBookShopApp.service;
import com.example.MyBookShopApp.dto.SuccessfulChangeBookStatusDto;
import com.example.MyBookShopApp.dtoAbstract.ChangeBookStatusDto;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.Book2User;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.Book2UserRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChangeBookStatusService {
    private final BookService bookService;
    private final RatingService ratingService;
    private final CookieService cookieService;
    private final UserRepository userRepository;
    private final Book2UserRepository book2UserRepository;
    private final static String POSTPONED_COOKIE_NAME = "postponedContents";
    private final static String CART_COOKIE_NAME = "cartContents";

    @Autowired
    public ChangeBookStatusService(BookService bookService,
                                   RatingService ratingService,
                                   CookieService cookieService,
                                   UserRepository userRepository,
                                   Book2UserRepository book2UserRepository) {
        this.bookService = bookService;
        this.ratingService = ratingService;
        this.cookieService = cookieService;
        this.userRepository = userRepository;
        this.book2UserRepository = book2UserRepository;
    }

    public ChangeBookStatusDto changeBookStatus(String[] booksIds, String status) {
        if (status == null) {
            status = "changeBookRating";
        }
        switch (status) {
            case "KEPT" -> {
                addBookToPostponed(booksIds);
            }
            case "UNLINK" -> {
                unlinkBook(booksIds);
            }
            case "CART" -> {
                addBookToCart(booksIds);
            }
            case "PAID" -> {
                savePurchasedBook(booksIds);
            }
            case "changeBookRating" -> {
                changeBookRatingData();
            }
        }
        return new SuccessfulChangeBookStatusDto(true);
    }

    private void addBookToPostponed(String[] booksIds) {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) return;

        HttpServletRequest request = ra.getRequest();
        HttpServletResponse response = ra.getResponse();
        if (response == null) return;

        boolean hasRequiredCookiesForContent = request.getCookies() != null && checkForRequiredCookies(request.getCookies());

        Cookie[] cookies = request.getCookies() != null && hasRequiredCookiesForContent ?
                request.getCookies() :
                new Cookie[] {new Cookie(CART_COOKIE_NAME, ""), new Cookie(POSTPONED_COOKIE_NAME, "")};

        String updatedCookieValue = updateCookieValue(booksIds);

        Set<Cookie> updatedCookies = updateCookie(
            cookies,
            String.valueOf(updatedCookieValue),
                POSTPONED_COOKIE_NAME
        );

        updatedCookies.forEach(cookie -> {
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }

    private void addBookToCart(String[] booksIds) {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) return;

        HttpServletRequest request = ra.getRequest();
        HttpServletResponse response = ra.getResponse();
        if (response == null) return;

        boolean hasRequiredCookiesForCartContent = request.getCookies() != null && checkForRequiredCookies(request.getCookies());

        Cookie[] cookies = request.getCookies() != null && hasRequiredCookiesForCartContent ?
            request.getCookies() :
            new Cookie[] {new Cookie(CART_COOKIE_NAME, ""), new Cookie(POSTPONED_COOKIE_NAME, "")};

        String updatedCookieValue = updateCookieValue(booksIds);

        Set<Cookie> updatedCookies = updateCookie(cookies, String.valueOf(updatedCookieValue), CART_COOKIE_NAME);

        updatedCookies.forEach(cookie -> {
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }

    private void unlinkBook(String[] booksIds) {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) return;

        HttpServletRequest request = ra.getRequest();
        HttpServletResponse response = ra.getResponse();
        if (response == null) return;

        boolean hasRequiredCookiesForCartContent = request.getCookies() != null && checkForRequiredCookies(request.getCookies());

        Cookie[] cookies = request.getCookies() != null && hasRequiredCookiesForCartContent ?
                request.getCookies() :
                new Cookie[] {new Cookie(CART_COOKIE_NAME, ""), new Cookie(POSTPONED_COOKIE_NAME, "")};

        String updatedCookieValue = updateCookieValue(booksIds);

        Set<Cookie> updatedCookies = updateCookie(cookies, String.valueOf(updatedCookieValue), "");

        updatedCookies.forEach(cookie -> {
            cookie.setPath("/");
            response.addCookie(cookie);
        });
    }

    @Transactional
    private void savePurchasedBook(String[] booksIds) {
        cookieService.clearCookie(CART_COOKIE_NAME);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByContact(authentication.getName()).orElseThrow();

        Arrays.stream(booksIds).forEach(id -> {
            Book book = bookService.getBookById(Integer.valueOf(id));
            Book2User book2User = new Book2User();
            book2User.setTime(new Date());
            book2User.setBook(book);
            book2User.setUser(currentUser);
            book2User.setIsPaid(1);
            book2UserRepository.save(book2User);
        });
    }

    private void changeBookRatingData() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) return;
        HttpServletRequest request = ra.getRequest();
        String bookId = request.getParameter("bookId");
        String rating = request.getParameter("value");

        ratingService.changeBookRating(Integer.valueOf(bookId), Integer.valueOf(rating));
    }

    private Set<Cookie> updateCookie(Cookie[] cookies, String updatedCookieValue, String cookieName) {
        return Arrays.stream(cookies)
            .map(cookie -> {
                String regex = "(?:^|/)" + "(" + updatedCookieValue.replaceAll("/", "|") + ")" + "(?=/|$)";
                return new Cookie(cookie.getName(), cookie.getValue().replaceAll(regex, ""));
            })
            .map(cookie -> {
                if (cookie.getName().equals(cookieName) && !cookie.getValue().equals("")) {
                    return new Cookie(cookie.getName(), cookie.getValue() + "/" + updatedCookieValue);
                }
                if (cookie.getName().equals(cookieName) && cookie.getValue().equals("")) {
                    return new Cookie(cookie.getName(), updatedCookieValue);
                }
                return cookie;
            })
            .collect(Collectors.toSet());
    }

    private String updateCookieValue(String[] booksIds) {
        if (booksIds == null) return "";
        StringJoiner updatedCookieValue = new StringJoiner("/");
        Arrays.stream(booksIds).forEach(bookId -> {
            String slug = bookService.getBookById(Integer.valueOf(bookId)).getSlug();
            updatedCookieValue.add(slug);
        });
        return String.valueOf(updatedCookieValue);
    }

    private boolean checkForRequiredCookies(Cookie[] cookies) {
        return Arrays.stream(cookies)
            .anyMatch(cookie -> cookie.getName().equals(CART_COOKIE_NAME) || cookie.getName().equals(POSTPONED_COOKIE_NAME));
    }
}
