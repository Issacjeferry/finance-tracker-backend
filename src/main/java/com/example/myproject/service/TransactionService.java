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
                .orElseThrow(() -> new RuntimeException("User not found"));
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

    // ✅ Calculate summary per user
    public Double getTotalIncome() {
        return getAll().stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Double getTotalExpense() {
        return getAll().stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
}