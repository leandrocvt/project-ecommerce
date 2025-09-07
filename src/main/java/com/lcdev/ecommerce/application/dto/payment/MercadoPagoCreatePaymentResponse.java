package com.lcdev.ecommerce.application.dto.payment;

public record MercadoPagoCreatePaymentResponse(
        String id,
        String status,
        String redirectUrl
) {
}
