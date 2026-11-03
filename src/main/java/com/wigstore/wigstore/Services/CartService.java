package com.wigstore.wigstore.service;

import com.wigstore.wigstore.model.CartItem;
import com.wigstore.wigstore.model.User;
import com.wigstore.wigstore.model.Wig;
import com.wigstore.wigstore.repository.CartItemRepository;
import com.wigstore.wigstore.repository.UserRepository;
import com.wigstore.wigstore.repository.WigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final WigRepository wigRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       WigRepository wigRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.wigRepository = wigRepository;
        this.userRepository = userRepository;
    }

    public List<CartItem> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem addToCart(Long userId, Long wigId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Wig wig = wigRepository.findById(wigId)
                .filter(Wig::isActive)
                .orElseThrow(() -> new IllegalArgumentException("This wig is not available"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // already in the cart? then add to that line instead of making a second one
        CartItem item = cartItemRepository.findByUserIdAndWigId(userId, wigId)
                .orElseGet(() -> {
                    CartItem fresh = new CartItem();
                    fresh.setUser(user);
                    fresh.setWig(wig);
                    fresh.setQuantity(0);
                    return fresh;
                });

        int newQuantity = item.getQuantity() + quantity;
        if (newQuantity > wig.getStockQuantity()) {
            throw new IllegalArgumentException("Only " + wig.getStockQuantity() + " in stock");
        }

        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(Long userId, Long wigId, int quantity) {
        CartItem item = cartItemRepository.findByUserIdAndWigId(userId, wigId)
                .orElseThrow(() -> new IllegalArgumentException("Item is not in your cart"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return;
        }
        if (quantity > item.getWig().getStockQuantity()) {
            throw new IllegalArgumentException("Not enough stock");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeFromCart(Long userId, Long wigId) {
        cartItemRepository.findByUserIdAndWigId(userId, wigId)
                .ifPresent(cartItemRepository::delete);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteAll(cartItemRepository.findByUserId(userId));
    }
}