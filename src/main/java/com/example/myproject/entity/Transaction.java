package com.example.myproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Type is required (INCOME or EXPENSE)")
    private String type;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
