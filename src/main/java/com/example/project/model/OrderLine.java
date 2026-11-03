
package com.example.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_code")
    private String productCode;

    private String description;

    private Double quantity;

    @Column(name = "line_total")
    private Double lineTotal;

    // Getters, Setters, and Constructors
}
