package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.payment.Payment;
import com.example.MyBookShopApp.model.BalanceTransaction;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.BalanceTransactionRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BalanceTransactionService {
    private final BalanceTransactionRepository balanceTransactionRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    @Autowired
    public BalanceTransactionService(BalanceTransactionRepository balanceTransactionRepository,
                                     UserRepository userRepository,
                                     BookService bookService) {
        this.balanceTransactionRepository = balanceTransactionRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
    }

    @Transactional
    public void saveUserBalanceTransaction(User currentUser, Payment payment, String balanceUpdateTransactionDescription) {
        BalanceTransaction balanceTransaction = new BalanceTransaction();
        balanceTransaction.setUser(currentUser);
        balanceTransaction.setTime(new Date());
        balanceTransaction.setValue((int) Math.round(Double.parseDouble(payment.getAmount().getValue())));
        balanceTransaction.setDescription(balanceUpdateTransactionDescription);
        balanceTransactionRepository.saveAndFlush(balanceTransaction);
        updateUserBalance(currentUser);
    }

    @Transactional
    public void SaveUserBalanceTransaction(User currentUser, Integer sum, String balanceUpdateTransactionDescription) {
        BalanceTransaction balanceTransaction = new BalanceTransaction();
        balanceTransaction.setUser(currentUser);
        balanceTransaction.setTime(new Date());
        balanceTransaction.setValue(-sum);
        balanceTransaction.setDescription(balanceUpdateTransactionDescription);
        balanceTransactionRepository.saveAndFlush(balanceTransaction);
        updateUserBalance(currentUser);
    }

    @Transactional
    public void updateUserBalance(User user) {
        Integer updatedUserBalance = balanceTransactionRepository.getUserCurrentBalance(user);
        user.setBalance(updatedUserBalance);
        userRepository.saveAndFlush(user);
    }

    public List<BalanceTransaction> getUserTransactions(User user) {
        List<BalanceTransaction> balanceTransactionList = balanceTransactionRepository.findByUserId(user.getId());
        try {
            balanceTransactionList.forEach(transaction -> {
                if (transaction.getDescription().contains("Покупка")) {
                    String[] descriptionArr = transaction.getDescription()
                        .replaceAll(",", "")
                        .trim()
                        .split(" ");
                    List<Book> bookList = Arrays.stream(descriptionArr)
                        .skip(2)
                        .map(bookService::getBookBySlug)
                        .collect(Collectors.toList());
                    transaction.setPurchasedBookList(bookList);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return balanceTransactionList;
    }
}
