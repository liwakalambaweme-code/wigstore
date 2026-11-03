package com.wigstore.wigstore.service.payment;

import com.wigstore.wigstore.model.Currency;
import java.math.BigDecimal;

public record PaymentRequest(Long orderId, BigDecimal amount, Currency currency, String payerPhone) {}