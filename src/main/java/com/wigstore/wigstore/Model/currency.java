package com.wigstore.wigstore.model;
import jakarta.persistence.*;

@Entity
@Table(name = "currency")
public class currency {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private String name;

    @Column (nullable= false) 
    private String description;

    @Column(nullable = false)
    private Double priceUsd;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int lengthInches;

    @Column

}
