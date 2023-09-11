package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.repository.RatingRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final TagService tagService;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(BookRepository bookRepository,
                       AuthorService authorService,
                       TagService tagService,
                       RatingRepository ratingRepository,
                       UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.tagService = tagService;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> bookPage = authorService.setBookAuthor(bookRepository.findAllByRating(nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Page<Book> getPageOfRecentBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = authorService.setBookAuthor(bookRepository.getAllBooksOrderingByPubDate(nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Page<Book> getPageOfRecentBooks(String from, String to, Integer offset, Integer limit) throws ParseException {
        Pageable nextPage = PageRequest.of(offset, limit);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        if (from == null) {
            Date dateTo = df.parse(to);
            Page<Book> bookPage = authorService.setBookAuthor(
                    bookRepository.findByPubDateBeforeOrderByPubDateAsc(dateTo, nextPage));
            return CalculateDiscountService.calculateDiscount(bookPage);
        } else if (to == null) {
            Date dateFrom = df.parse(from);
            Page<Book> bookPage = authorService.setBookAuthor(
                    bookRepository.findByPubDateAfterOrderByPubDateAsc(dateFrom, nextPage));
            return CalculateDiscountService.calculateDiscount(bookPage);
        } else {
            Date dateFrom = df.parse(from);
            Date dateTo = df.parse(to);
            Page<Book> bookPage = authorService.setBookAuthor(
                    bookRepository.findByPubDateBetweenOrderByPubDateAsc(dateFrom, dateTo, nextPage));
            return CalculateDiscountService.calculateDiscount(bookPage);
        }
    }

    public Page<Book> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> bookPage = authorService.setBookAuthor(
                bookRepository.findBookByTitleContaining(searchWord,nextPage));
        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Page<Book> getBooksByAuthorSlug(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> bookPage = authorService.setBookAuthor(bookRepository.findByAuthorSlug(slug, nextPage));

        return CalculateDiscountService.calculateDiscount(bookPage);
    }

    public Book getBookBySlug(String slug) {
        Book book = bookRepository.findBySlug(slug);
        book.setAuthor(authorService.getAuthorByBookSlug(slug));

        return CalculateDiscountService.calculateDiscount(book);
    }

    public List<Book> getBySlugIn(String[] cookieSlugs) {
        List<Book> books = bookRepository.findBySlugIn(cookieSlugs);
        books.forEach(book -> book.setAuthor(authorService.getAuthorByBookSlug(book.getSlug())));
        return CalculateDiscountService.calculateDiscount(bookRepository.findBySlugIn(cookieSlugs));
    }

    @Transactional
    public void updatePostponedData(String bookId, String status) {
        switch (status) {
            case "KEPT" -> {
                if (bookRepository.existsById(Integer.parseInt(bookId))) {
                    Book book = increasePostponedNumber(Integer.parseInt(bookId));
                    bookRepository.save(book);
                }
            }
            case "UNLINK" ->{
                if (bookRepository.existsById(Integer.parseInt(bookId))) {
                    Book book = reducePostponedNumber(Integer.parseInt(bookId));
                    bookRepository.save(book);
                }
            }
        }
    }

    @Transactional
    public void updateBookCartData(String booksIds, String status) {
        switch (status) {
            case "CART" -> {
                if (bookRepository.existsById(Integer.parseInt(booksIds))) {
                    Book book = increaseCartNumber(Integer.parseInt(booksIds));
                    bookRepository.save(book);
                }
            }
            case "UNLINK" ->{
                if (bookRepository.existsById(Integer.parseInt(booksIds))) {
                    Book book = reduceCartNumber(Integer.parseInt(booksIds));
                    bookRepository.save(book);
                }
            }
        }
    }

    public Boolean checkBookIsKeptOrCart(String cookie, String slug) {
        return cookie != null && !cookie.equals("") && cookie.contains(slug);
    }

    private Book increasePostponedNumber(Integer id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        Book book = optionalBook.orElseThrow();
        book.setPostponedNumber(book.getPostponedNumber() + 1);

        return book;
    }

    private Book reducePostponedNumber(Integer id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        Book book = optionalBook.orElseThrow();
        book.setPostponedNumber(book.getPostponedNumber() - 1);

        return book;
    }

    private Book increaseCartNumber(Integer id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        Book book = optionalBook.orElseThrow();
        book.setCartNumber(book.getCartNumber() + 1);

        return book;
    }

    private Book reduceCartNumber(Integer id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        Book book = optionalBook.orElseThrow();
        book.setCartNumber(book.getCartNumber() - 1);

        return book;
    }

    public Book getBookById(Integer bookId) {
        return bookRepository.findById(bookId).orElseThrow();
    }

    public String generateBooksIdsStringForTagAttribute(List<Book> bookList) {
        return bookList.stream()
            .map(Book::getId)
            .collect(Collectors.toList())
            .toString()
            .replace("[", "")
            .replace("]", "")
            .trim();
    }

    public List<Book> getUserBooks(User currentUser) {
        List<Book> userBooks = authorService.setBookAuthor(bookRepository.getAllUserBookByUserId(currentUser.getId()));
        return CalculateDiscountService.calculateDiscount(userBooks);
    }

    public Boolean checkIsPaidBook(Book book) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        if (userName.equals("AnonymousUser")) return false;

        User user = userRepository.findByContact(userName).orElseThrow();
        List<Book> userBooks = bookRepository.getAllUserBookByUserId(user.getId());
        return userBooks.contains(book);
    }
}