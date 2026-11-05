package com.wigstore.wigstore.config;

import com.wigstore.wigstore.model.Role;
import com.wigstore.wigstore.model.User;
import com.wigstore.wigstore.repository.UserRepository;
import com.wigstore.wigstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final String email;
    private final String password;

    public AdminSeeder(UserService userService, UserRepository userRepository,
                       @Value("${app.admin.email:}") String email,
                       @Value("${app.admin.password:}") String password) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) {
            log.info("ADMIN_EMAIL / ADMIN_PASSWORD not set, skipping admin account");
            return;
        }
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User admin = userService.register("Admin", email, "0000000000", password, false);
        userService.changeRole(admin.getId(), Role.ADMIN);
        log.info("Admin account created for {}", email);
    }
}