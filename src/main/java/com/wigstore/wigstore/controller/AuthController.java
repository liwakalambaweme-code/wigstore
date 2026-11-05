package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.model.User;
import com.wigstore.wigstore.service.TokenService;
import com.wigstore.wigstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final ApiMapper mapper;

    public AuthController(UserService userService, TokenService tokenService, ApiMapper mapper) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest r) {
        User user = userService.register(
                r.name(), r.email(), r.phoneNumber(), r.password(), r.emailConsent());
        return new AuthResponse(tokenService.createToken(user), mapper.toUser(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest r) {
        User user = userService.authenticate(r.email(), r.password());
        return new AuthResponse(tokenService.createToken(user), mapper.toUser(user));
    }
}