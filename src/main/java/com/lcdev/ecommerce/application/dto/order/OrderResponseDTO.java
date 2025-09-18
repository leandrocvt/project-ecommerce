package com.lcdev.ecommerce.application.dto.order;

import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.projections.OrderProjection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public record OrderResponseDTO(
        Long id,
        Instant moment,
        OrderStatus status,
        BigDecimal total,
        String clientName,
        Instant paymentMoment

) {

    public static OrderResponseDTO fromProjection(OrderProjection p) {
        BigDecimal subtotal = p.getSubtotal() != null ? p.getSubtotal() : BigDecimal.ZERO;
        BigDecimal shipping = p.getShippingCost() != null ? p.getShippingCost() : BigDecimal.ZERO;
        BigDecimal discount = p.getDiscountApplied() != null ? p.getDiscountApplied() : BigDecimal.ZERO;

        BigDecimal total = subtotal
                .subtract(discount)
                .add(shipping)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        return new OrderResponseDTO(
                p.getId(),
                p.getMoment(),
                p.getStatus(),
                total,
                p.getClientName(),
                p.getPaymentMoment()
        );
    }

}
