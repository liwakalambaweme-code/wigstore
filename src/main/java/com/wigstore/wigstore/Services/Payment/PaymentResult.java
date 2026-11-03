package com.wigstore.wigstore.service.payment;

import com.wigstore.wigstore.model.PaymentStatus;

public record PaymentResult(PaymentStatus status, String reference, String message) {}