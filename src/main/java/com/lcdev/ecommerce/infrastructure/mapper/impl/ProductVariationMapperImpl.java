package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.ProductRequestDTO;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import com.lcdev.ecommerce.infrastructure.mapper.ProductVariationMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class ProductVariationMapperImpl implements ProductVariationMapper {

    @Override
    public void updateVariations(ProductRequestDTO dto, Product entity) {
        if (dto.getVariations() != null) {
            entity.getVariations().clear();

            dto.getVariations().forEach(variationDTO -> {
                ProductVariation variation = new ProductVariation();
                variation.setColor(variationDTO.getColor());
                variation.setSize(variationDTO.getSize());
                variation.setPriceAdjustment(variationDTO.getPriceAdjustment());
                variation.setStockQuantity(variationDTO.getStockQuantity());
                variation.setDiscountAmount(
                        Optional.ofNullable(variationDTO.getDiscountAmount()).orElse(BigDecimal.ZERO)
                );
                variation.setProduct(entity);

                if (variationDTO.getImages() != null) {
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

                entity.getVariations().add(variation);
            });
        }
    }
}
