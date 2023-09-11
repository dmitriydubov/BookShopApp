package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.BalanceTransaction;
import com.example.MyBookShopApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransaction, Integer> {
    @Query("SELECT SUM(bt.value) FROM BalanceTransaction bt WHERE user = :user")
    Integer getUserCurrentBalance(User user);

    List<BalanceTransaction> findByUserId(Integer id);
}
