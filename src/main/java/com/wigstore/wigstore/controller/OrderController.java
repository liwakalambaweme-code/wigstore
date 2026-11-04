package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.model.OrderStatus;
import com.wigstore.wigstore.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ApiMapper mapper;

    public OrderController(OrderService orderService, ApiMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    // checkout: turns the customer's cart into an order
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(@RequestHeader("X-User-Id") Long userId,
                                  @RequestBody CheckoutRequest r) {
        return mapper.toOrder(orderService.placeOrder(
                userId, r.deliveryAddress(), r.city(), r.deliveryNotes()));
    }

    @GetMapping
    public List<OrderResponse> mine(@RequestHeader("X-User-Id") Long userId) {
        return orderService.getOrdersForCustomer(userId).stream().map(mapper::toOrder).toList();
    }

    @GetMapping("/{id}")
    public OrderResponse one(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return mapper.toOrder(orderService.getOrderForCustomer(id, userId));
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancel(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return mapper.toOrder(orderService.cancelOwnOrder(id, userId));
    }

    // seller/admin: orders by status (defaults to the ones waiting)
    @GetMapping("/manage")
    public List<OrderResponse> manage(@RequestParam(defaultValue = "PENDING") OrderStatus status) {
        return orderService.getOrdersByStatus(status).stream().map(mapper::toOrder).toList();
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @RequestBody StatusRequest r) {
        return mapper.toOrder(orderService.updateStatus(id, r.status()));
    }
}