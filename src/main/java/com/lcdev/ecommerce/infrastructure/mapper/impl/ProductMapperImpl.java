package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.ProductRequestDTO;
import com.lcdev.ecommerce.application.dto.ProductResponseDTO;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import com.lcdev.ecommerce.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductRequestDTO dto, Category category) {
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setBasePrice(dto.getBasePrice());
        entity.setCategory(category);

        if (dto.getVariations() != null) {
            entity.setVariations(
                    dto.getVariations().stream().map(variationDTO -> {
                        ProductVariation variation = new ProductVariation();
                        variation.setColor(variationDTO.getColor());
                        variation.setSize(variationDTO.getSize());
                        variation.setPriceAdjustment(variationDTO.getPriceAdjustment());
                        variation.setStockQuantity(variationDTO.getStockQuantity());
                        variation.setDiscountAmount(Optional.ofNullable(variationDTO.getDiscountAmount())
                                .orElse(BigDecimal.ZERO));
                        variation.setProduct(entity);

                        if (Objects.nonNull(variationDTO.getImages())) {
                            List<ProductVariationImage> images = variationDTO.getImages().stream()
                                    .map(imgDTO -> {
                                        ProductVariationImage img = new ProductVariationImage();
                                        img.setImgUrl(imgDTO.getImgUrl());
                                        img.setPrimary(imgDTO.isPrimary());
                                        img.setVariation(variation);
                                        return img;
                                    }).toList();
                            variation.setImages(images);
                        }

                        return variation;
                    }).toList()
            );
        }

        return entity;
    }

    @Override
    public ProductResponseDTO toResponseDTO(Product entity) {
        return new ProductResponseDTO(entity);
    }

}
