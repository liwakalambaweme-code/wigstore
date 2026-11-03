package com.wigstore.wigstore.event;

import com.wigstore.wigstore.model.*;
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

    public NotificationListener(NotificationService notificationService,
                                EmailService emailService,
                                UserRepository userRepository) {
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.userRepository = userRepository;
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
    }

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        Order order = event.order();
        String forStaff = "New order #" + order.getId() + " from "
                + order.getCustomer().getName() + " ($" + order.getTotalUsd() + ")";
        for (User staff : staff()) {
            notificationService.notify(staff, NotificationType.ORDER_PLACED, forStaff);
        }
        notificationService.notify(order.getCustomer(), NotificationType.ORDER_PLACED,
                "Your order #" + order.getId() + " was received");
    }

    @EventListener
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Payment payment = event.payment();
        Order order = payment.getOrder();
        for (User staff : staff()) {
            notificationService.notify(staff, NotificationType.PAYMENT_RECEIVED,
                    "Payment received for order #" + order.getId());
        }
        notificationService.notify(order.getCustomer(), NotificationType.PAYMENT_RECEIVED,
                "We received your payment for order #" + order.getId());
    }

    private List<User> staff() {
        List<User> staff = new ArrayList<>(userRepository.findByRole(Role.SELLER));
        staff.addAll(userRepository.findByRole(Role.ADMIN));
        return staff;
    }
}