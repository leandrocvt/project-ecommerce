package com.lcdev.ecommerce.application.dto;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items,
        String couponCode
) {
}
