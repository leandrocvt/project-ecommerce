package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.product.ProductRequestDTO;
import com.lcdev.ecommerce.application.dto.product.ProductVariationRequestDTO;
import com.lcdev.ecommerce.application.dto.product.ProductVariationUpdateDTO;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.ProductVariation;

public interface ProductVariationMapper {

    void applyVariationsFromDTO(ProductRequestDTO dto, Product entity);

    void updateEntity(ProductVariationUpdateDTO dto, ProductVariation variation);

    ProductVariation toEntity(ProductVariationRequestDTO dto, Product product);

}
