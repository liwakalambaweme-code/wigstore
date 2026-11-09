package com.wigstore.wigstore.service;

import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistItemRepository wishlistRepository;
    private final WigRepository wigRepository;
    private final UserRepository userRepository;

    public WishlistService(WishlistItemRepository wishlistRepository,
                           WigRepository wigRepository,
                           UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wigRepository = wigRepository;
        this.userRepository = userRepository;
    }

    public List<WishlistItem> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Transactional
    public WishlistItem addToWishlist(Long userId, Long wigId) {
        // already there? return it as is
        return wishlistRepository.findByUserIdAndWigId(userId, wigId).orElseGet(() -> {
            Wig wig = wigRepository.findById(wigId)
                    .filter(Wig::isActive)
                    .orElseThrow(() -> new IllegalArgumentException("This wig is not available"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            WishlistItem item = new WishlistItem();
            item.setUser(user);
            item.setWig(wig);
            return wishlistRepository.save(item);
        });
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long wigId) {
        wishlistRepository.findByUserIdAndWigId(userId, wigId)
                .ifPresent(wishlistRepository::delete);
    }
}
