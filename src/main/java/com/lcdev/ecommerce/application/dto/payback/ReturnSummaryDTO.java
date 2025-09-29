package com.lcdev.ecommerce.application.dto.payback;

import com.lcdev.ecommerce.domain.enums.ReturnStatus;
import com.lcdev.ecommerce.infrastructure.projections.ReturnProjection;

import java.math.BigDecimal;
import java.time.Instant;

public record ReturnSummaryDTO(
        Long id,
        Instant createdAt,
        ReturnStatus status,
        String clientName,
        Long orderId,
        BigDecimal totalRefund
) {
    public static ReturnSummaryDTO fromProjection(ReturnProjection p) {
        return new ReturnSummaryDTO(
                p.getId(),
                p.getCreatedAt(),
                p.getStatus(),
                p.getClientName(),
                p.getOrderId(),
                p.getTotalRefund()
        );
    }
}


