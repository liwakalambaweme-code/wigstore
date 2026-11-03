package com.wigstore.wigstore.service;

import com.wigstore.wigstore.event.OrderPlacedEvent;
import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final WigRepository wigRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        WigRepository wigRepository,
                        UserRepository userRepository,
                        ApplicationEventPublisher events) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.wigRepository = wigRepository;
        this.userRepository = userRepository;
        this.events = events;
    }

    // checkout: cart -> order
    @Transactional
    public Order placeOrder(Long userId, String deliveryAddress, String city, String deliveryNotes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<CartItem> cart = cartItemRepository.findByUserId(userId);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }

        Order order = new Order();
        order.setCustomer(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setCity(city);
        order.setDeliveryNotes(deliveryNotes);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart) {
            Wig wig = cartItem.getWig();

            // availability can change while a wig sits in a cart, so check again now
            if (!wig.isActive()) {
                throw new IllegalArgumentException(wig.getName() + " is no longer available");
            }
            if (cartItem.getQuantity() > wig.getStockQuantity()) {
                throw new IllegalArgumentException("Not enough stock for " + wig.getName());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setWig(wig);
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPriceUsd(wig.getPriceUsd());   // price snapshot (the receipt rule)
            order.getItems().add(item);

            wig.setStockQuantity(wig.getStockQuantity() - cartItem.getQuantity());
            wigRepository.save(wig);

            total = total.add(wig.getPriceUsd().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalUsd(total);

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteAll(cart);
        events.publishEvent(new OrderPlacedEvent(saved));
        return saved;
    }

    public List<Order> getOrdersForCustomer(Long userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderForCustomer(Long orderId, Long userId) {
        Order order = getOrder(orderId);
        if (!order.getCustomer().getId().equals(userId)) {
            throw new IllegalArgumentException("Order not found");   // don't reveal it exists
        }
        return order;
    }

    // seller/admin views
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("A cancelled order cannot be changed");
        }
        if (newStatus == OrderStatus.CANCELLED) {
            restock(order);
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // a customer may only cancel their own order, and only before it is paid
    @Transactional
    public Order cancelOwnOrder(Long orderId, Long userId) {
        Order order = getOrderForCustomer(orderId, userId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only unpaid orders can be cancelled");
        }
        return updateStatus(orderId, OrderStatus.CANCELLED);
    }

    private void restock(Order order) {
        for (OrderItem item : order.getItems()) {
            Wig wig = item.getWig();
            wig.setStockQuantity(wig.getStockQuantity() + item.getQuantity());
            wigRepository.save(wig);
        }
    }
}