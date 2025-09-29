package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.payback.ReturnItemResponseDTO;
import com.lcdev.ecommerce.application.dto.payback.ReturnResponseDTO;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.infrastructure.mapper.ReturnMapper;
import org.springframework.stereotype.Component;

@Component
public class ReturnMapperImpl implements ReturnMapper {

    @Override
    public ReturnResponseDTO toResponse(ReturnRequest entity) {
        return new ReturnResponseDTO(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getStatus(),
                entity.getRequestedAt(),
                entity.getDeadline(),
                entity.getItems().stream()
                        .map(this::toResponse)
                        .toList(),
                entity.calculateTotalRefundAmount()
        );
    }

    @Override
    public ReturnItemResponseDTO toResponse(ReturnItem entity) {
        OrderItem orderItem = entity.getOrderItem();
        ProductVariation variation = orderItem != null ? orderItem.getVariation() : null;
        Product product = variation != null ? variation.getProduct() : null;

        return new ReturnItemResponseDTO(
                entity.getId(),
                variation != null ? variation.getId() : null,
                product != null ? product.getName() : "Produto não encontrado",
                entity.getQuantity(),
                entity.getStatus(),
                entity.getReason(),
                entity.getComment(),
                entity.getRefundAmount()
        );
    }
}


