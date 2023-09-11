package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.payment.Amount;
import com.example.MyBookShopApp.dto.payment.Confirmation;
import com.example.MyBookShopApp.dto.payment.Payment;
import com.example.MyBookShopApp.model.Transaction;
import com.example.MyBookShopApp.model.User;
import com.example.MyBookShopApp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class PaymentService {
    private final TransactionRepository transactionRepository;
    private final BalanceTransactionService balanceTransactionService;

    @Value(value = "${yookassa.urlapi}")
    private String urlApi;

    @Value(value = "${yookassa.shopId}")
    private String shopId;

    @Value(value = "${yookassa.access.token}")
    private String accessToken;

    private final static String PENDING_PAYMENT = "pending";
    private final static String SUCCEED_PAYMENT = "succeeded";

    @Autowired
    public PaymentService(TransactionRepository transactionRepository,
                          BalanceTransactionService balanceTransactionService) {
        this.transactionRepository = transactionRepository;
        this.balanceTransactionService = balanceTransactionService;
    }

    public Payment createPayment(Payment payment, String userHash, String time) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            String idempotenceKey = UUID.randomUUID().toString();
            httpHeaders.setBasicAuth(shopId, accessToken);
            httpHeaders.set("Idempotence-Key", idempotenceKey);

            HttpEntity<Payment> request = new HttpEntity<>(payment, httpHeaders);
            ResponseEntity<Payment> response = restTemplate.exchange(
                    urlApi + "/payments",
                    HttpMethod.POST,
                    request,
                    Payment.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.getBody() != null) {
                    saveTransaction(response.getBody(), userHash, time);
                }
                return response.getBody();
            } else {
                throw new RuntimeException("Error handled!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            payment.setStatus("canceled");
        }

        return payment;
    }

    public Payment generatePayment(String sum) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        Amount amount = new Amount();
        amount.setValue(sum);
        amount.setCurrency("RUB");
        payment.setAmount(amount);
        Confirmation confirmation = new Confirmation();
        confirmation.setType("redirect");
        confirmation.setReturnUrl("http://localhost:8085/profile");
        payment.setConfirmation(confirmation);
        payment.setTest(true);
        payment.setCapture(true);
        return payment;
    }

    @Transactional
    private void saveTransaction(Payment payment, String userHash, String time) {
        Transaction transaction = new Transaction();
        transaction.setPaymentId(payment.getId());
        transaction.setUserHash(userHash);
        Date date = new Date(Long.parseLong(time));
        transaction.setTime(date);
        transaction.setStatus(PENDING_PAYMENT);
        transactionRepository.save(transaction);
    }

    public void updateUserTransactions(User currentUser) {
        List<Transaction> transactionList =
                transactionRepository.findByUserHashAndStatus(currentUser.getHash(), PENDING_PAYMENT);
        if (transactionList.size() == 0) return;
        try {
            transactionList.forEach(transaction -> {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(shopId, accessToken);
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<Payment> response = restTemplate.exchange(
                        urlApi + "/payments/" + transaction.getPaymentId(),
                        HttpMethod.GET,
                        request,
                        Payment.class
                );
                Payment updatedPayment = response.getBody();
                String updatedStatus = Objects.requireNonNull(response.getBody()).getStatus();
                if (updatedPayment != null && updatedStatus.equals(SUCCEED_PAYMENT)) {
                    String userUpdateBalanceDescription = "Пополнение счёта";
                    transaction.setStatus(updatedStatus);
                    transactionRepository.save(transaction);
                    balanceTransactionService.saveUserBalanceTransaction(currentUser, updatedPayment, userUpdateBalanceDescription);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
