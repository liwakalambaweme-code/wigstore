package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final ApiMapper mapper;

    public CartController(CartService cartService, ApiMapper mapper) {
        this.cartService = cartService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CartItemResponse> get(@RequestHeader("X-User-Id") Long userId) {
        return cartService.getCart(userId).stream().map(mapper::toCartItem).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse add(@RequestHeader("X-User-Id") Long userId,
                                @RequestBody AddToCartRequest r) {
        return mapper.toCartItem(cartService.addToCart(userId, r.wigId(), r.quantity()));
    }

    @PutMapping("/{wigId}")
    public void setQuantity(@RequestHeader("X-User-Id") Long userId,
                            @PathVariable Long wigId, @RequestBody QuantityRequest r) {
        cartService.updateQuantity(userId, wigId, r.quantity());
    }

    @DeleteMapping("/{wigId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@RequestHeader("X-User-Id") Long userId, @PathVariable Long wigId) {
        cartService.removeFromCart(userId, wigId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
    }
}