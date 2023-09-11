package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserHashAndStatus(String hash, String pending);
}
