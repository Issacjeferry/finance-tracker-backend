package com.example.myproject.repository;


import com.example.myproject.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.myproject.entity.User;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.type = 'INCOME'")
    Double getTotalIncome();

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.type = 'EXPENSE'")
    Double getTotalExpense();

    List<Transaction> findByUser(User user);
}

