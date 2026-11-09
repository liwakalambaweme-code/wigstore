package com.wigstore.wigstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountUsd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currencyPaid = Currency.USD;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    // the USD to ZMW rate used at payment time (1 if paid in USD)
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal exchangeRateUsed = BigDecimal.ONE;

    private String transactionReference;
    private String payerPhone;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime paidAt;   // stays null until payment completes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public Long getId() { return id; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public BigDecimal getAmountUsd() { return amountUsd; }
    public void setAmountUsd(BigDecimal amountUsd) { this.amountUsd = amountUsd; }

    public Currency getCurrencyPaid() { return currencyPaid; }
    public void setCurrencyPaid(Currency currencyPaid) { this.currencyPaid = currencyPaid; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getExchangeRateUsed() { return exchangeRateUsed; }
    public void setExchangeRateUsed(BigDecimal exchangeRateUsed) { this.exchangeRateUsed = exchangeRateUsed; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getPayerPhone() { return payerPhone; }
    public void setPayerPhone(String payerPhone) { this.payerPhone = payerPhone; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}