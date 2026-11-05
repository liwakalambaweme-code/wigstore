package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final ApiMapper mapper;

    public UserController(UserService userService, ApiMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    // settings page
    @GetMapping("/api/users/me")
    public UserResponse me(@CurrentUserId Long userId) {
        return mapper.toUser(userService.getUser(userId));
    }

    @PutMapping("/api/users/me")
    public UserResponse updateMe(@CurrentUserId Long userId, @RequestBody UpdateProfileRequest r) {
        return mapper.toUser(userService.updateProfile(userId, r.name(), r.phoneNumber(),
                r.preferredCurrency(), r.theme(), r.emailConsent()));
    }

    // admin only (enforced in SecurityConfig)
    @GetMapping("/api/admin/users")
    public List<UserResponse> allUsers() {
        return userService.listAll().stream().map(mapper::toUser).toList();
    }

    @PutMapping("/api/admin/users/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @RequestBody ChangeRoleRequest r) {
        return mapper.toUser(userService.changeRole(id, r.role()));
    }
}