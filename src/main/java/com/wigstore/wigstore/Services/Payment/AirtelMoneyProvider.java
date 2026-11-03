package com.wigstore.wigstore.service.payment;

import com.wigstore.wigstore.model.PaymentMethod;
import com.wigstore.wigstore.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AirtelMoneyProvider implements PaymentProvider {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.AIRTEL_MONEY;
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        // PLACEHOLDER: this does not charge anyone. The real version will call Airtel's
        // payment API, which needs merchant credentials from Airtel and their API docs.
        // Mobile money is normally asynchronous: the customer approves a prompt on their
        // phone, so we return PENDING and confirm later.
        String reference = "AIRTEL-" + UUID.randomUUID().toString().substring(0, 8);
        return new PaymentResult(PaymentStatus.PENDING, reference,
                "Approve the payment request on your phone");
    }
}