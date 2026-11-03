package com.wigstore.wigstore.service.payment;

import com.wigstore.wigstore.model.PaymentMethod;

public interface PaymentProvider {
    PaymentMethod getMethod();
    PaymentResult pay(PaymentRequest request);
}
