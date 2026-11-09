package com.wigstore.wigstore.service.payment;

import com.wigstore.wigstore.model.PaymentMethod;
import com.wigstore.wigstore.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BankTransferProvider implements PaymentProvider {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.BANK_TRANSFER;
    }

    @Override
    public PaymentResult pay(PaymentRequest request) {
        // the customer pays outside the app, and the seller confirms once the money arrives
        String reference = "BANK-" + UUID.randomUUID().toString().substring(0, 8);
        return new PaymentResult(PaymentStatus.PENDING, reference,
                "Transfer the amount and quote this reference: " + reference);
    }
}