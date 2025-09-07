package com.lcdev.ecommerce.application.dto.payment;

import com.lcdev.ecommerce.domain.enums.PaymentStatus;

public record PaymentStatusResponse(
        String transactionId,
        PaymentStatus status
) {
}
