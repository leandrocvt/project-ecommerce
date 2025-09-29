package com.lcdev.ecommerce.application.dto.payback;

import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.ReturnItem;
import com.lcdev.ecommerce.domain.enums.ReturnStatus;

import java.math.BigDecimal;

public record ReturnItemResponseDTO(
        Long id,
        Long variationId,
        String productName,
        Integer quantity,
        ReturnStatus status,
        String reason,
        String comment,
        BigDecimal refundAmount
) {
    public static ReturnItemResponseDTO fromEntity(ReturnItem entity) {
        ProductVariation variation = entity.getOrderItem().getVariation();
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

