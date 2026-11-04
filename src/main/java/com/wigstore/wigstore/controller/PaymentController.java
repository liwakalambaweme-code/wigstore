package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final ApiMapper mapper;

    public PaymentController(PaymentService paymentService, ApiMapper mapper) {
        this.paymentService = paymentService;
        this.mapper = mapper;
    }

    @PostMapping("/api/orders/{orderId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@RequestHeader("X-User-Id") Long userId,
                               @PathVariable Long orderId, @RequestBody PayRequest r) {
        return mapper.toPayment(paymentService.startPayment(
                orderId, userId, r.method(), r.currency(), r.payerPhone()));
    }

    @GetMapping("/api/orders/{orderId}/payments")
    public List<PaymentResponse> forOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentsForOrder(orderId).stream().map(mapper::toPayment).toList();
    }

    // seller: the money has arrived
    @PutMapping("/api/payments/{id}/confirm")
    public PaymentResponse confirm(@PathVariable Long id) {
        return mapper.toPayment(paymentService.confirmPayment(id));
    }

    @PutMapping("/api/payments/{id}/fail")
    public PaymentResponse fail(@PathVariable Long id) {
        return mapper.toPayment(paymentService.markFailed(id));
    }
}