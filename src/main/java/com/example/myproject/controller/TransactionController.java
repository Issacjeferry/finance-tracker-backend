package com.example.myproject.controller;

import com.example.myproject.dto.SummaryResponse;
import com.example.myproject.entity.Transaction;
import com.example.myproject.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody Transaction transaction) {
        Transaction created = service.save(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id,
                                              @Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(service.update(id, transaction));
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> summary() {
        return ResponseEntity.ok(service.getSummary());
    }

    @GetMapping("/summary/income")
    public ResponseEntity<Double> totalIncome() {
        return ResponseEntity.ok(service.getTotalIncome());
    }

    @GetMapping("/summary/expense")
    public ResponseEntity<Double> totalExpense() {
        return ResponseEntity.ok(service.getTotalExpense());
    }

    @GetMapping("/summary/balance")
    public ResponseEntity<Double> balance() {
        return ResponseEntity.ok(service.getBalance());
    }
}

