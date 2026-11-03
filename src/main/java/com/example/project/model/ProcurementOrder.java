package com.example.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "procurement_orders")
public class ProcurementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "expected_next_month_demand")
    private Double expectedNextMonthDemand;

    @Column(name = "calculated_safety_stock")
    private Double calculatedSafetyStock;

    @Column(name = "lead_time_weeks")
    private Integer leadTimeWeeks;

    @Column(name = "suggested_units_to_buy")
    private Integer suggestedUnitsToBuy;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "status", nullable = false)
    private String status; // Values: PENDING, APPROVED, REJECTED

    public ProcurementOrder() {
    }

    public ProcurementOrder(String productCode, Double expectedNextMonthDemand, Double calculatedSafetyStock, Integer leadTimeWeeks, Integer suggestedUnitsToBuy) {
        this.productCode = productCode;
        this.expectedNextMonthDemand = expectedNextMonthDemand;
        this.calculatedSafetyStock = calculatedSafetyStock;
        this.leadTimeWeeks = leadTimeWeeks;
        this.suggestedUnitsToBuy = suggestedUnitsToBuy;
        this.generatedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Double getExpectedNextMonthDemand() {
        return expectedNextMonthDemand;
    }

    public void setExpectedNextMonthDemand(Double expectedNextMonthDemand) {
        this.expectedNextMonthDemand = expectedNextMonthDemand;
    }

    public Double getCalculatedSafetyStock() {
        return calculatedSafetyStock;
    }

    public void setCalculatedSafetyStock(Double calculatedSafetyStock) {
        this.calculatedSafetyStock = calculatedSafetyStock;
    }

    public Integer getLeadTimeWeeks() {
        return leadTimeWeeks;
    }

    public void setLeadTimeWeeks(Integer leadTimeWeeks) {
        this.leadTimeWeeks = leadTimeWeeks;
    }

    public Integer getSuggestedUnitsToBuy() {
        return suggestedUnitsToBuy;
    }

    public void setSuggestedUnitsToBuy(Integer suggestedUnitsToBuy) {
        this.suggestedUnitsToBuy = suggestedUnitsToBuy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
