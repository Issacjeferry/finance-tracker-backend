package com.example.myproject.controller;

import com.example.myproject.entity.Transaction;
import com.example.myproject.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }



    @PostMapping
    public Transaction create(@Valid @RequestBody Transaction transaction) {
        return service.save(transaction);
    }


    @GetMapping
    public List<Transaction> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public Transaction update(@PathVariable Long id,
                              @Valid @RequestBody Transaction transaction) {
        return service.update(id, transaction);
    }


    @GetMapping("/summary/income")
    public Double totalIncome() {
        return service.getTotalIncome();
    }

    @GetMapping("/summary/expense")
    public Double totalExpense() {
        return service.getTotalExpense();
    }

    @GetMapping("/summary/balance")
    public Double balance() {
        return service.getBalance();
    }


}

