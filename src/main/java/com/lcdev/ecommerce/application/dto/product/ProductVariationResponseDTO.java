package com.lcdev.ecommerce.application.dto.product;

import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.enums.Color;
import com.lcdev.ecommerce.domain.enums.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationResponseDTO {

    private Long id;
    private Color color;
    private Size size;
    private BigDecimal priceAdjustment;
    private BigDecimal discountAmount;
    private Integer stockQuantity;
    private List<ProductVariationImageDTO> images;

    public ProductVariationResponseDTO(ProductVariation variation) {
        id = variation.getId();
        color = variation.getColor();
        size = variation.getSize();
        priceAdjustment = variation.getPriceAdjustment();
        discountAmount = variation.getDiscountAmount();
        stockQuantity = variation.getStockQuantity();
        if (Objects.nonNull(variation.getImages())) {
            images = variation.getImages().stream()
                    .map(img -> new ProductVariationImageDTO(img.getId(), img.getImgUrl(), img.isPrimary()))
                    .toList();
        }
    }

    public static ProductVariationResponseDTO from(ProductVariation variation) {
        return new ProductVariationResponseDTO(variation);
    }

}
