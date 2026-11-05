package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.OrderService;
import com.wigstore.wigstore.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final ApiMapper mapper;

    public PaymentController(PaymentService paymentService, OrderService orderService, ApiMapper mapper) {
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping("/api/orders/{orderId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@CurrentUserId Long userId,
                               @PathVariable Long orderId, @RequestBody PayRequest r) {
        return mapper.toPayment(paymentService.startPayment(
                orderId, userId, r.method(), r.currency(), r.payerPhone()));
    }

    // customers see payments for their own orders only; seller and admin see any
    @GetMapping("/api/orders/{orderId}/payments")
    public List<PaymentResponse> forOrder(@CurrentUserId Long userId, Authentication auth,
                                          @PathVariable Long orderId) {
        boolean staff = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_SELLER") || a.equals("ROLE_ADMIN"));
        if (!staff) {
            orderService.getOrderForCustomer(orderId, userId);   // throws if it isn't theirs
        }
        return paymentService.getPaymentsForOrder(orderId).stream().map(mapper::toPayment).toList();
    }

    @PutMapping("/api/payments/{id}/confirm")
    public PaymentResponse confirm(@PathVariable Long id) {
        return mapper.toPayment(paymentService.confirmPayment(id));
    }

    @PutMapping("/api/payments/{id}/fail")
    public PaymentResponse fail(@PathVariable Long id) {
        return mapper.toPayment(paymentService.markFailed(id));
    }
}