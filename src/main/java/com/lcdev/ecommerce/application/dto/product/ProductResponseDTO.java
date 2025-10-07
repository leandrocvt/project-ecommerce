package com.lcdev.ecommerce.application.dto.product;

import com.lcdev.ecommerce.domain.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Long categoryId;
    private Boolean active;
    private Integer stockQuantity;
    private List<ProductVariationResponseDTO> variations;

    public ProductResponseDTO(Product product) {
        id = product.getId();
        name = product.getName();
        description = product.getDescription();
        basePrice = product.getBasePrice();
        categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        active = product.getActive();
        stockQuantity = product.getStockQuantity();

        if (product.getVariations() != null) {
            this.variations = product.getVariations()
                    .stream()
                    .map(ProductVariationResponseDTO::new)
                    .toList();
        }
    }
}
