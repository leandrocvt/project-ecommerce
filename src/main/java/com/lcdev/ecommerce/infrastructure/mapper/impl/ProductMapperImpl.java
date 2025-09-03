package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.assessment.AssessmentResponseDTO;
import com.lcdev.ecommerce.application.dto.product.*;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.infrastructure.mapper.ProductMapper;
import com.lcdev.ecommerce.infrastructure.mapper.ProductVariationMapper;
import com.lcdev.ecommerce.infrastructure.projections.AssessmentProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationProjection;
import com.lcdev.ecommerce.infrastructure.projections.ReviewSummaryProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (dto.getActive() != null) entity.setActive(dto.getActive());
    }

    @Override
    public ProductDetailsResponseDTO toDetailsDTO(
            List<ProductVariationProjection> variationsProjections,
            List<ProductVariationImageProjection> imagesProjections,
            ReviewSummaryProjection reviewSummary,
            List<AssessmentProjection> sampleReviews) {

        if (variationsProjections.isEmpty()) {
            return null;
        }

        ProductVariationProjection first = variationsProjections.get(0);

        // Agrupa imagens por variationId
        Map<Long, List<ProductVariationImageDTO>> imagesByVariation = imagesProjections.stream()
                .map(img -> new ProductVariationImageDTO(img.getImageId(), img.getImgUrl(), img.getPrimary()))
                .collect(Collectors.groupingBy(
                        img -> imagesProjections.stream()
                                .filter(p -> p.getImageId().equals(img.getId()))
                                .findFirst()
                                .get()
                                .getVariationId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Cria variações com imagens agrupadas
        List<ProductVariationDTO> variations = variationsProjections.stream()
                .map(v -> new ProductVariationDTO(
                        v.getVariationId(),
                        v.getColor(),
                        v.getSize(),
                        v.getPriceAdjustment(),
                        v.getDiscountAmount(),
                        v.getVariationStock(),
                        imagesByVariation.getOrDefault(v.getVariationId(), new ArrayList<>())
                ))
                .toList();

        // Mapeia sample reviews
        List<AssessmentResponseDTO> sample = sampleReviews.stream()
                .map(AssessmentResponseDTO::new)
                .toList();

        return new ProductDetailsResponseDTO(
                first.getProductId(),
                first.getName(),
                first.getDescription(),
                first.getBasePrice(),
                first.getCategoryId(),
                first.getActive(),
                0,
                variations,
                reviewSummary.getAverageScore(),
                reviewSummary.getTotalReviews(),
                sample
        );
    }


    @Override
    public ProductResponseDTO toResponseDTO(Product entity) {
        return new ProductResponseDTO(entity);
    }

}
