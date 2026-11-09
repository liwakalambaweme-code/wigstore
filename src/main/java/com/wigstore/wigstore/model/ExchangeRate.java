package com.wigstore.wigstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate rateDate;          // one rate per day

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal usdToZmw;

    private LocalDateTime fetchedAt = LocalDateTime.now();

    public Long getId() { return id; }

    public LocalDate getRateDate() { return rateDate; }
    public void setRateDate(LocalDate rateDate) { this.rateDate = rateDate; }

    public BigDecimal getUsdToZmw() { return usdToZmw; }
    public void setUsdToZmw(BigDecimal usdToZmw) { this.usdToZmw = usdToZmw; }

    public LocalDateTime getFetchedAt() { return fetchedAt; }
}