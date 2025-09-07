package com.lcdev.ecommerce.infrastructure.payment.mercadopago.exceptions;

public class MercadoPagoException extends RuntimeException {
    private final int statusCode;
    private final String details;

    public MercadoPagoException(String message, int statusCode, String details) {
        super(message);
        this.statusCode = statusCode;
        this.details = details;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDetails() {
        return details;
    }
}