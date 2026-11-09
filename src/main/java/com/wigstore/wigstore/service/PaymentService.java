package com.wigstore.wigstore.service;

import com.wigstore.wigstore.event.PaymentCompletedEvent;
import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.repository.OrderRepository;
import com.wigstore.wigstore.repository.PaymentRepository;
import com.wigstore.wigstore.service.payment.PaymentProvider;
import com.wigstore.wigstore.service.payment.PaymentRequest;
import com.wigstore.wigstore.service.payment.PaymentResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ExchangeRateService exchangeRateService;
    private final ApplicationEventPublisher events;
    private final Map<PaymentMethod, PaymentProvider> providers;

    // Spring injects every PaymentProvider it finds; this service never names a specific one
    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          ExchangeRateService exchangeRateService,
                          ApplicationEventPublisher events,
                          List<PaymentProvider> providerList) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.exchangeRateService = exchangeRateService;
        this.events = events;
        this.providers = providerList.stream()
                .collect(Collectors.toMap(PaymentProvider::getMethod, Function.identity()));
    }

    @Transactional
    public Payment startPayment(Long orderId, Long userId, PaymentMethod method,
                                Currency currency, String payerPhone) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getCustomer().getId().equals(userId)) {
            throw new IllegalArgumentException("Order not found");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("This order cannot be paid");
        }
        PaymentProvider provider = providers.get(method);
        if (provider == null) {
            throw new IllegalArgumentException("This payment method is not available yet");
        }

        // record the rate used, so the payment can be audited later
        BigDecimal rate = (currency == Currency.USD) ? BigDecimal.ONE : exchangeRateService.getTodayRate();
        BigDecimal amountPaid = order.getTotalUsd().multiply(rate).setScale(2, RoundingMode.HALF_UP);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setAmountUsd(order.getTotalUsd());
        payment.setCurrencyPaid(currency);
        payment.setAmountPaid(amountPaid);
        payment.setExchangeRateUsed(rate);
        payment.setPayerPhone(payerPhone);
        payment = paymentRepository.save(payment);

        PaymentResult result = provider.pay(new PaymentRequest(orderId, amountPaid, currency, payerPhone));
        payment.setStatus(result.status());
        payment.setTransactionReference(result.reference());
        if (result.status() == PaymentStatus.COMPLETED) {
            markCompleted(payment);
        }
        return paymentRepository.save(payment);
    }

    // the seller (or later, a provider callback) confirms that the money has arrived
    @Transactional
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return payment;   // confirming twice changes nothing
        }
        markCompleted(payment);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markFailed(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setStatus(PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsForOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    private void markCompleted(Payment payment) {
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        events.publishEvent(new PaymentCompletedEvent(payment));
    }
}