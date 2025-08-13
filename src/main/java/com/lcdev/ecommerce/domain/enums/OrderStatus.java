package com.lcdev.ecommerce.domain.enums;

public enum OrderStatus {
    WAITING_PAYMENT,
    PAID,
    INVOICE_ISSUED,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELED
}
