package com.lcdev.ecommerce.application.dto.payment;

import com.lcdev.ecommerce.domain.enums.PaymentStatus;

public record PaymentResponse(
        String transactionId,
        PaymentStatus status,
        String redirectUrl,
        String pixCode
) {
}
