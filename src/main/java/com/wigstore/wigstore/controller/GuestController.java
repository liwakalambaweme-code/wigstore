package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.OrderService;
import com.wigstore.wigstore.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// everything a visitor without an account needs to buy something
@RestController
@RequestMapping("/api/guest")
public class GuestController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ApiMapper mapper;

    public GuestController(OrderService orderService, PaymentService paymentService, ApiMapper mapper) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.mapper = mapper;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(@RequestBody GuestCheckoutRequest r) {
        return mapper.toGuestOrder(orderService.placeGuestOrder(
                r.name(), r.email(), r.phone(), r.items(),
                r.deliveryAddress(), r.city(), r.deliveryNotes()));
    }

    @GetMapping("/orders/{id}")
    public OrderResponse one(@PathVariable Long id, @RequestParam String token) {
        return mapper.toOrder(orderService.getGuestOrder(id, token));
    }

    @PostMapping("/orders/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@PathVariable Long id, @RequestBody GuestPayRequest r) {
        return mapper.toPayment(paymentService.startGuestPayment(
                id, r.token(), r.method(), r.currency(), r.payerPhone()));
    }
}