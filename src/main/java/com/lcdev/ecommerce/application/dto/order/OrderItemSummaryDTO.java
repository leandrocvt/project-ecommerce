package com.lcdev.ecommerce.application.dto.order;

public record OrderItemSummaryDTO(
        Long variationId,
        String productName,
        String imageUrl,
        Integer quantity
) {
}
