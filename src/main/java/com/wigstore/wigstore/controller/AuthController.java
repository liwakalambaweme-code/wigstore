package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ApiMapper mapper;

    public AuthController(UserService userService, ApiMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest r) {
        return mapper.toUser(userService.register(
                r.name(), r.email(), r.phoneNumber(), r.password(), r.emailConsent()));
    }

    // for now returns the user; the security step will return a login token
    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest r) {
        return mapper.toUser(userService.authenticate(r.email(), r.password()));
    }
}