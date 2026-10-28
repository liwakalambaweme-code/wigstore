package com.wigstore.wigstore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")

public class User {

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

   
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name){this.name= name;}

    public String getEmail() { return email; }
    public void setEmail(String email){this.email= email;}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber){this.phoneNumber=phoneNumber;}

    public String getPassword() { return password; }
    public void setPassword(String password){this.password= password;}

    public Role getRole() { return role; }
    public void setRole(Role role){this.role= role;}

    public boolean isEmailConsent() { return emailConsent; }
    public void setIsEmailConsent(emailConsent){this.emailConsent;}

    public Currency getPreferredCurrency() { return preferredCurrency; }
    public void setPreferredCurrency(Currency preferredCurrency){this.preferredCurrency = preferredCurrency;}

    public ThemePreference getTheme() { return theme; }
    public void setThemePreference(ThemePreference theme){this.theme= theme;}

    public LocalDateTime getCreatedAt() { return createdAt; }

   
}