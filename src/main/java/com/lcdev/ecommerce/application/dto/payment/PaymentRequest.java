package com.lcdev.ecommerce.application.dto.payment;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String token,
        String cardBrand,
        Integer installments,
        String payerEmail,
        String payerIdentificationType,
        String payerIdentificationNumber
) {
}
