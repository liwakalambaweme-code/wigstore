package com.wigstore.wigstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wigs")
public class Wig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceUsd;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int lengthInches;

    @Column(nullable = false)
    private int densityPercent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HairType hairType;   // no default: the seller must choose

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPriceUsd() { return priceUsd; }
    public void setPriceUsd(BigDecimal priceUsd) { this.priceUsd = priceUsd; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getLengthInches() { return lengthInches; }
    public void setLengthInches(int lengthInches) { this.lengthInches = lengthInches; }

    public int getDensityPercent() { return densityPercent; }
    public void setDensityPercent(int densityPercent) { this.densityPercent = densityPercent; }

    public HairType getHairType() { return hairType; }
    public void setHairType(HairType hairType) { this.hairType = hairType; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}