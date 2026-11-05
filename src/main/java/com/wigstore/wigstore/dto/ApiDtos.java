package com.wigstore.wigstore.dto;

import com.wigstore.wigstore.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiDtos {

    private ApiDtos() {}

    // users
    public record RegisterRequest(String name, String email, String phoneNumber,
                                  String password, boolean emailConsent) {}
    public record LoginRequest(String email, String password) {}
    public record UserResponse(Long id, String name, String email, String phoneNumber,
                               Role role, Currency preferredCurrency,
                               ThemePreference theme, boolean emailConsent) {}
    public record UpdateProfileRequest(String name, String phoneNumber,
                                       Currency preferredCurrency,
                                       ThemePreference theme, boolean emailConsent) {}
    public record ChangeRoleRequest(Role role) {}

    // wigs
    public record WigRequest(String name, String description, BigDecimal priceUsd,
                             String color, int lengthInches, int densityPercent,
                             HairType hairType, int stockQuantity) {}
    public record ImageResponse(Long id, String url, boolean primaryImage, int displayOrder) {}
    public record WigResponse(Long id, String name, String description,
                              BigDecimal priceUsd, BigDecimal displayPrice,
                              Currency displayCurrency, String color,
                              int lengthInches, int densityPercent, HairType hairType,
                              int stockQuantity, boolean active,
                              String coverImageUrl, List<ImageResponse> images) {}

    // cart
    public record AddToCartRequest(Long wigId, int quantity) {}
    public record QuantityRequest(int quantity) {}
    public record CartItemResponse(Long wigId, String wigName, BigDecimal unitPriceUsd,
                                   int quantity, String coverImageUrl) {}

    // orders
    public record CheckoutRequest(String deliveryAddress, String city, String deliveryNotes) {}
    public record StatusRequest(OrderStatus status) {}
    public record OrderItemResponse(Long wigId, String wigName, int quantity,
                                    BigDecimal unitPriceUsd) {}
    public record OrderResponse(Long id, OrderStatus status, BigDecimal totalUsd,
                                LocalDateTime createdAt, String deliveryAddress,
                                String city, String deliveryNotes, String customerName,
                                List<OrderItemResponse> items) {}

    // payments
    public record PayRequest(PaymentMethod method, Currency currency, String payerPhone) {}
    public record PaymentResponse(Long id, Long orderId, PaymentMethod method,
                                  PaymentStatus status, BigDecimal amountUsd,
                                  Currency currencyPaid, BigDecimal amountPaid,
                                  String transactionReference,
                                  LocalDateTime createdAt, LocalDateTime paidAt) {}

    // notifications and exchange rate
    public record NotificationResponse(Long id, NotificationType type, String message,
                                       boolean seen, LocalDateTime createdAt) {}
    public record RateRequest(BigDecimal usdToZmw) {}
    public record AuthResponse(String token, UserResponse user) {}
}