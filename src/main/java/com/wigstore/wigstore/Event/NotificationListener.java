package com.wigstore.wigstore.event;

import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.repository.SubscriberRepository;
import com.wigstore.wigstore.repository.UserRepository;
import com.wigstore.wigstore.service.EmailService;
import com.wigstore.wigstore.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationListener {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SubscriberRepository subscriberRepository;

    public NotificationListener(NotificationService notificationService,
                                EmailService emailService,
                                UserRepository userRepository,
                                SubscriberRepository subscriberRepository) {
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.subscriberRepository = subscriberRepository;
    }

    @EventListener
    public void onWigCreated(WigCreatedEvent event) {
        Wig wig = event.wig();
        String message = "New wig available: " + wig.getName();

        // in-app notification for every customer
        for (User customer : userRepository.findByRole(Role.CUSTOMER)) {
            notificationService.notify(customer, NotificationType.NEW_WIG, message);
        }
        // email only for those who gave consent
        for (User customer : userRepository.findByRoleAndEmailConsentTrue(Role.CUSTOMER)) {
            emailService.send(customer.getEmail(), "New wig: " + wig.getName(), message);
        }
        // and guests who ticked "keep me updated" after buying
        for (Subscriber s : subscriberRepository.findAll()) {
            emailService.send(s.getEmail(), "New wig: " + wig.getName(), message);
        }
    }

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        Order order = event.order();
        String forStaff = "New order #" + order.getId() + " from "
                + order.contactName() + " ($" + order.getTotalUsd() + ")";
        for (User staff : staff()) {
            notificationService.notify(staff, NotificationType.ORDER_PLACED, forStaff);
        }
        if (order.getCustomer() != null) {
            notificationService.notify(order.getCustomer(), NotificationType.ORDER_PLACED,
                    "Your order #" + order.getId() + " was received");
        }
        emailService.send(order.contactEmail(), "We received your order #" + order.getId(),
                "Thank you, " + order.contactName() + ". Order #" + order.getId()
                        + " total $" + order.getTotalUsd() + ".\n" + lines(order));
    }

    private String lines(Order order) {
        StringBuilder sb = new StringBuilder();
        for (OrderItem i : order.getItems()) {
            sb.append(i.getQuantity()).append(" x ").append(i.getWig().getName())
              .append(" @ $").append(i.getUnitPriceUsd()).append("\n");
        }
        return sb.toString();
    }

    @EventListener
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Payment payment = event.payment();
        Order order = payment.getOrder();
        for (User staff : staff()) {
            notificationService.notify(staff, NotificationType.PAYMENT_RECEIVED,
                    "Payment received for order #" + order.getId());
        }
        if (order.getCustomer() != null) {
            notificationService.notify(order.getCustomer(), NotificationType.PAYMENT_RECEIVED,
                    "We received your payment for order #" + order.getId());
        }
        emailService.send(order.contactEmail(), "Receipt for order #" + order.getId(),
                "Payment received. Total $" + order.getTotalUsd() + ".\n" + lines(order));
    }

    private List<User> staff() {
        List<User> staff = new ArrayList<>(userRepository.findByRole(Role.SELLER));
        staff.addAll(userRepository.findByRole(Role.ADMIN));
        return staff;
    }
}