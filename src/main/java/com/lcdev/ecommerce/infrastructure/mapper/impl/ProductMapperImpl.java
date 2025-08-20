package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.ProductRequestDTO;
import com.lcdev.ecommerce.application.dto.ProductResponseDTO;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import com.lcdev.ecommerce.infrastructure.mapper.ProductMapper;
import com.lcdev.ecommerce.infrastructure.mapper.ProductVariationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {

    private final ProductVariationMapper productVariationMapper;

    @Override
    public Product toEntity(ProductRequestDTO dto, Category category) {
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setBasePrice(dto.getBasePrice());
        entity.setCategory(category);

        productVariationMapper.updateVariations(dto, entity);

        return entity;
    }

    public void updateBasicFields(ProductRequestDTO dto, Product entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getBasePrice() != null) entity.setBasePrice(dto.getBasePrice());
    }


    @Override
    public ProductResponseDTO toResponseDTO(Product entity) {
        return new ProductResponseDTO(entity);
    }

}
