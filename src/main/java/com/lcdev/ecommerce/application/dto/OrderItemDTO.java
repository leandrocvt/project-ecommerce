package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.OrderItem;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderItemDTO {

    private Long variationId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private String imageUrl;

    public OrderItemDTO(OrderItem entity, String imageUrl) {
        this.variationId = entity.getVariation().getId();
        this.productName = entity.getVariation().getProduct().getName();
        this.price = entity.getVariation().getFinalPrice();
        this.quantity = entity.getQuantity();
        this.subtotal = this.price.multiply(BigDecimal.valueOf(this.quantity))
                .setScale(2, RoundingMode.HALF_UP);
        this.imageUrl = imageUrl;
    }

}
