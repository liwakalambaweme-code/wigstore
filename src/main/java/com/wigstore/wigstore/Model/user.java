package com.wigstore.wigstore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class user {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password; // stored hashed, never plain text

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    private boolean emailConsent = false;

    @Enumerated(EnumType.STRING)
    private Currency preferredCurrency = Currency.USD;

    @Enumerated(EnumType.STRING)
    private ThemePreference theme = ThemePreference.SYSTEM;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public boolean isEmailConsent() { return emailConsent; }
    public Currency getPreferredCurrency() { return preferredCurrency; }
    public ThemePreference getTheme() { return theme; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // setters (same pattern for each field)
}