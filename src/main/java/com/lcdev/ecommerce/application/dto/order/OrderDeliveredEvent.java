package com.lcdev.ecommerce.application.dto.order;

public record OrderDeliveredEvent(Long orderId, Long userId) {}
