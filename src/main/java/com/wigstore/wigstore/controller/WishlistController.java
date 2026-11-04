package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.WigResponse;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.model.Currency;
import com.wigstore.wigstore.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final ApiMapper mapper;

    public WishlistController(WishlistService wishlistService, ApiMapper mapper) {
        this.wishlistService = wishlistService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<WigResponse> get(@RequestHeader("X-User-Id") Long userId,
                                 @RequestParam(defaultValue = "USD") Currency currency) {
        return wishlistService.getWishlist(userId).stream()
                .map(item -> mapper.toWig(item.getWig(), currency)).toList();
    }

    @PostMapping("/{wigId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestHeader("X-User-Id") Long userId, @PathVariable Long wigId) {
        wishlistService.addToWishlist(userId, wigId);
    }

    @DeleteMapping("/{wigId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@RequestHeader("X-User-Id") Long userId, @PathVariable Long wigId) {
        wishlistService.removeFromWishlist(userId, wigId);
    }
}