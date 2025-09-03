package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.product.ProductRequestDTO;
import com.lcdev.ecommerce.domain.entities.Product;

public interface ProductVariationMapper {

    void updateVariations(ProductRequestDTO dto, Product entity);
}
