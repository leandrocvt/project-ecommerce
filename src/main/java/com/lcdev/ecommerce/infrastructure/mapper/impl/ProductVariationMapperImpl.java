package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.product.ProductRequestDTO;
import com.lcdev.ecommerce.application.dto.product.ProductVariationRequestDTO;
import com.lcdev.ecommerce.application.dto.product.ProductVariationUpdateDTO;
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
    public void applyVariationsFromDTO(ProductRequestDTO dto, Product entity) {
        if (dto.getVariations() != null) {
            entity.getVariations().clear();

            dto.getVariations().forEach(variationDTO -> {
                ProductVariation variation = new ProductVariation();
                variation.setColor(variationDTO.getColor());
                variation.setSize(variationDTO.getSize());
                variation.setPriceAdjustment(variationDTO.getPriceAdjustment());
                variation.setStockQuantity(variationDTO.getStockQuantity());
                variation.setDiscountAmount(
                        Optional.ofNullable(variationDTO.getDiscountAmount())
                                .orElse(BigDecimal.ZERO)
                );
                variation.setProduct(entity);

                entity.getVariations().add(variation);
            });
        }
    }

    @Override
    public void updateEntity(ProductVariationUpdateDTO dto, ProductVariation variation) {
        if (dto.color() != null) variation.setColor(dto.color());
        if (dto.size() != null) variation.setSize(dto.size());
        if (dto.priceAdjustment() != null) variation.setPriceAdjustment(dto.priceAdjustment());
        if (dto.discountAmount() != null) variation.setDiscountAmount(dto.discountAmount());
        if (dto.stockQuantity() != null) variation.setStockQuantity(dto.stockQuantity());
    }

    @Override
    public ProductVariation toEntity(ProductVariationRequestDTO dto, Product product) {
        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setColor(dto.getColor());
        variation.setSize(dto.getSize());
        variation.setPriceAdjustment(dto.getPriceAdjustment());
        variation.setDiscountAmount(Optional.ofNullable(dto.getDiscountAmount()).orElse(BigDecimal.ZERO));
        variation.setStockQuantity(dto.getStockQuantity());
        return variation;
    }

}

