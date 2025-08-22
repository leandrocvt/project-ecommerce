package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationImageDTO {
    private Long id;
    private String imgUrl;
    private boolean primary;

    public ProductVariationImageDTO(ProductVariationImage entity) {
        id = entity.getId();
        imgUrl = entity.getImgUrl();
        primary = entity.isPrimary();
    }
}
