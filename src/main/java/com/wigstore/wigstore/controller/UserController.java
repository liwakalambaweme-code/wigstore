package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.UserService;
import org.springframework.web.bind.annotation.*;

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
    public UserResponse me(@RequestHeader("X-User-Id") Long userId) {
        return mapper.toUser(userService.getUser(userId));
    }

    @PutMapping("/api/users/me")
    public UserResponse updateMe(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody UpdateProfileRequest r) {
        return mapper.toUser(userService.updateProfile(userId, r.name(), r.phoneNumber(),
                r.preferredCurrency(), r.theme(), r.emailConsent()));
    }

    // admin only once security exists: promote an account to SELLER
    @PutMapping("/api/admin/users/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @RequestBody ChangeRoleRequest r) {
        return mapper.toUser(userService.changeRole(id, r.role()));
    }
}