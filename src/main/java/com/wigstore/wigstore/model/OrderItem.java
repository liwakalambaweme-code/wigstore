package com.wigstore.wigstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity = 1;

    // price at the moment of purchase, copied from the wig (the "receipt" rule)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPriceUsd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wig_id", nullable = false)
    private Wig wig;

    public Long getId() { return id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPriceUsd() { return unitPriceUsd; }
    public void setUnitPriceUsd(BigDecimal unitPriceUsd) { this.unitPriceUsd = unitPriceUsd; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Wig getWig() { return wig; }
    public void setWig(Wig wig) { this.wig = wig; }
}
