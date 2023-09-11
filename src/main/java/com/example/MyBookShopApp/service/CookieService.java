package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class CookieService {

    private final BookService bookService;
    private final RatingService ratingService;

    @Autowired
    public CookieService(BookService bookService,
                         RatingService ratingService) {
        this.bookService = bookService;
        this.ratingService = ratingService;
    }

    public List<Book> getListOfBookSlugFromCookie(String cookieValue, List<Book> booksContentList) {
        cookieValue = cookieValue.startsWith("/") ? cookieValue.substring(1) : cookieValue;
        cookieValue = cookieValue.endsWith("/") ? cookieValue.substring(0, booksContentList.size() - 1) : cookieValue;
        String[] booksSlug = cookieValue.split("/");
        List<Book> booksFromBooksSlug = bookService.getBySlugIn(booksSlug);
        booksFromBooksSlug.forEach(ratingService::setBookRatingStars);
        return booksFromBooksSlug;
    }

    public void clearCookie(String cookieName) {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) return;
        HttpServletResponse response = ra.getResponse();
        if (response == null) return;
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
