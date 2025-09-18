package com.lcdev.ecommerce.application.dto.order;

import com.lcdev.ecommerce.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryDTO(
        Long id,
        Instant moment,
        OrderStatus status,
        String paymentMethod,
        BigDecimal total,
        String primaryImageUrl
) {
}
