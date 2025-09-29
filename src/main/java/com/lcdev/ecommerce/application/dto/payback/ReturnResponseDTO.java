package com.lcdev.ecommerce.application.dto.payback;

import com.lcdev.ecommerce.domain.entities.ReturnRequest;
import com.lcdev.ecommerce.domain.enums.ReturnStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ReturnResponseDTO(
        Long id,
        Long orderId,
        ReturnStatus status,
        Instant requestedAt,
        Instant deadline,
        List<ReturnItemResponseDTO> items,
        BigDecimal totalRefundAmount
) {
    public static ReturnResponseDTO fromEntity(ReturnRequest entity) {
        return new ReturnResponseDTO(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getStatus(),
                entity.getRequestedAt(),
                entity.getDeadline(),
                entity.getItems().stream()
                        .map(ReturnItemResponseDTO::fromEntity)
                        .toList(),
                entity.calculateTotalRefundAmount()
        );
    }
}


