package com.wigstore.wigstore.service;
import java.util.List;
import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> listAll() {
    return userRepository.findAll();
}

    @Transactional
    public User register(String name, String email, String phoneNumber,
                         String rawPassword, boolean emailConsent) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmailConsent(emailConsent);
        user.setRole(Role.CUSTOMER);   // never taken from the request
        return userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Wrong email or password"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Wrong email or password");
        }
        return user;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // settings page: details, currency, theme, email consent
    @Transactional
    public User updateProfile(Long userId, String name, String phoneNumber,
                              Currency preferredCurrency, ThemePreference theme,
                              boolean emailConsent) {
        User user = getUser(userId);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setPreferredCurrency(preferredCurrency);
        user.setTheme(theme);
        user.setEmailConsent(emailConsent);
        return userRepository.save(user);
    }

    // admin only (the controller enforces who may call this)
    @Transactional
    public User changeRole(Long userId, Role newRole) {
        User user = getUser(userId);
        user.setRole(newRole);
        return userRepository.save(user);
    }
}