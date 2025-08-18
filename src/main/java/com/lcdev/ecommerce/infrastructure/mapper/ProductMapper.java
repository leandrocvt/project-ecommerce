package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.*;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;

public interface ProductMapper {

    Product toEntity(ProductRequestDTO dto, Category category);

    ProductResponseDTO toResponseDTO(Product entity);
}
