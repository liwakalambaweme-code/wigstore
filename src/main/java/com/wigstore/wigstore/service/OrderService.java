package com.wigstore.wigstore.service;

import com.wigstore.wigstore.event.OrderPlacedEvent;
import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wigstore.wigstore.dto.ApiDtos.OrderLineRequest;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    // checkout with the cart held in the browser: a logged-in customer sends the lines directly
    @Transactional
    public Order placeOrderForUser(Long userId, List<OrderLineRequest> lines,
                                   String deliveryAddress, String city, String deliveryNotes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Order order = new Order();
        order.setCustomer(user);
        return fillAndSave(order, lines, deliveryAddress, city, deliveryNotes);
    }

    // guest checkout: no account, just contact details (the receipt goes to this email)
    @Transactional
    public Order placeGuestOrder(String name, String email, String phone,
                                 List<OrderLineRequest> lines,
                                 String deliveryAddress, String city, String deliveryNotes) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Your name is required");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("A valid email is required");
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("A phone number is required");

        Order order = new Order();
        order.setGuestName(name.trim());
        order.setGuestEmail(email.trim());
        order.setGuestPhone(phone.trim());
        order.setGuestToken(UUID.randomUUID().toString());
        return fillAndSave(order, lines, deliveryAddress, city, deliveryNotes);
    }

    public Order getGuestOrder(Long orderId, String token) {
        Order order = getOrder(orderId);
        if (order.getGuestToken() == null || !order.getGuestToken().equals(token)) {
            throw new IllegalArgumentException("Order not found");   // don't reveal it exists
        }
        return order;
    }

    private Order fillAndSave(Order order, List<OrderLineRequest> lines,
                              String deliveryAddress, String city, String deliveryNotes) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Choose at least one wig");
        }
        // the same wig listed twice becomes one line
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderLineRequest line : lines) {
            if (line.wigId() == null || line.quantity() < 1) {
                throw new IllegalArgumentException("Invalid item in your order");
            }
            quantities.merge(line.wigId(), line.quantity(), Integer::sum);
        }

        order.setDeliveryAddress(deliveryAddress);
        order.setCity(city);
        order.setDeliveryNotes(deliveryNotes);

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Wig wig = wigRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("A wig in your order no longer exists"));
            int qty = entry.getValue();
            if (!wig.isActive()) {
                throw new IllegalArgumentException(wig.getName() + " is no longer available");
            }
            if (qty > wig.getStockQuantity()) {
                throw new IllegalArgumentException("Not enough stock for " + wig.getName());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setWig(wig);
            item.setQuantity(qty);
            item.setUnitPriceUsd(wig.getPriceUsd());   // price snapshot
            order.getItems().add(item);

            wig.setStockQuantity(wig.getStockQuantity() - qty);
            wigRepository.save(wig);
            total = total.add(wig.getPriceUsd().multiply(BigDecimal.valueOf(qty)));
        }
        order.setTotalUsd(total);

        Order saved = orderRepository.save(order);
        events.publishEvent(new OrderPlacedEvent(saved));
        return saved;
    }

    public List<Order> getOrdersForCustomer(Long userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderForCustomer(Long orderId, Long userId) {
        Order order = getOrder(orderId);
        if (order.getCustomer() == null || !order.getCustomer().getId().equals(userId)) {
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