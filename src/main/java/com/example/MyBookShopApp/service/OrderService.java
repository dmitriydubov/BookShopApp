package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.SucceedOrderDto;
import com.example.MyBookShopApp.dtoAbstract.OrderDto;
import com.example.MyBookShopApp.dtoErrors.ErrorOrderDto;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final BalanceTransactionService balanceTransactionService;
    private final ChangeBookStatusService changeBookStatusService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final static String USER_NOT_FOUND = "Пользователь не найден!";
    private final static String NOT_ENOUGH_MONEY_FOR_TRANSACTION = "Недостаточно денег для покупки! Пожалуйста, пополните счет";
    private final static String ZERO_SUM = "Ошибка оплаты! Сумма заказа не может быть равна 0!";

    @Autowired
    public OrderService(BalanceTransactionService balanceTransactionService,
                        ChangeBookStatusService changeBookStatusService,
                        UserRepository userRepository,
                        BookRepository bookRepository) {
        this.balanceTransactionService = balanceTransactionService;
        this.changeBookStatusService = changeBookStatusService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }


    public OrderDto makeOrder(String[] booksIds, String sum) {
        try {
            if (Integer.parseInt(sum) == 0) return new ErrorOrderDto(false, ZERO_SUM);
            int totalPrice = Integer.parseInt(sum.replaceAll("\\D", "").trim());

            Optional<User> optionalUser = userRepository.findByContact(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName());
            if (optionalUser.isEmpty()) return new ErrorOrderDto(false, USER_NOT_FOUND);

            User user = optionalUser.get();
            if (user.getBalance() < totalPrice) return new ErrorOrderDto(false, NOT_ENOUGH_MONEY_FOR_TRANSACTION);

            List<Book> bookList = bookRepository.findBooksByIds(Arrays.stream(booksIds)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList())
            );

            StringBuilder sb = new StringBuilder();
            if (bookList.size() > 1) {
                sb.append("Покупка книг: ");
                bookList.forEach(book -> {
                    sb.append(book.getSlug());
                    sb.append(", ");
                });
            } else {
                sb.append("Покупка книги ");
                sb.append(bookList.get(0).getSlug());
            }

            String description = sb.toString().trim();
            balanceTransactionService.SaveUserBalanceTransaction(
                    user,
                    totalPrice,
                    description
            );

            changeBookStatusService.changeBookStatus(booksIds, "PAID");

            return new SucceedOrderDto(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new SucceedOrderDto(true);
    }
}
