package com.example.myproject.service;

import com.example.myproject.entity.Transaction;
import com.example.myproject.entity.User;
import com.example.myproject.repository.TransactionRepository;
import com.example.myproject.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository repository,
                              UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // 🔐 Get logged-in user
    private User getCurrentUser() {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Unauthorized: User not found"));
    }

    // ✅ Save transaction for logged-in user
    public Transaction save(Transaction transaction) {

        User user = getCurrentUser();
        transaction.setUser(user);

        return repository.save(transaction);
    }

    // ✅ Get only current user's transactions
    public List<Transaction> getAll() {
        return repository.findByUser(getCurrentUser());
    }

    // ✅ Get transaction only if belongs to user
    public Transaction getById(Long id) {

        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId()
                .equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return transaction;
    }

    // ✅ Delete only if belongs to user
    public void delete(Long id) {

        Transaction transaction = getById(id);
        repository.delete(transaction);
    }

    // ✅ Update only if belongs to user
    public Transaction update(Long id, Transaction updatedTransaction) {

        Transaction existing = getById(id);

        existing.setTitle(updatedTransaction.getTitle());
        existing.setAmount(updatedTransaction.getAmount());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setType(updatedTransaction.getType());
        existing.setDate(updatedTransaction.getDate());
        existing.setDescription(updatedTransaction.getDescription());

        return repository.save(existing);
    }

    // ✅ Consolidated summary per user
    public com.example.myproject.dto.SummaryResponse getSummary() {
        List<Transaction> list = getAll();
        double income = 0.0;
        double expense = 0.0;
        for (Transaction t : list) {
            if (t.getAmount() != null) {
                if ("INCOME".equalsIgnoreCase(t.getType())) {
                    income += t.getAmount();
                } else if ("EXPENSE".equalsIgnoreCase(t.getType())) {
                    expense += t.getAmount();
                }
            }
        }
        return com.example.myproject.dto.SummaryResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income - expense)
                .transactionCount(list.size())
                .build();
    }

    // ✅ Calculate summary per user (legacy endpoints)
    public Double getTotalIncome() {
        return getSummary().getTotalIncome();
    }

    public Double getTotalExpense() {
        return getSummary().getTotalExpense();
    }

    public Double getBalance() {
        return getSummary().getBalance();
    }
}