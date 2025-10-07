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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {

    private final ProductVariationMapper productVariationMapper;

    public void updateBasicFields(ProductUpdateDTO dto, Product entity) {
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.description() != null) entity.setDescription(dto.description());
        if (dto.basePrice() != null) entity.setBasePrice(dto.basePrice());
        if (dto.active() != null) entity.setActive(dto.active());
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

        // ✅ Agrupa imagens por variationId (mais eficiente)
        Map<Long, List<ProductVariationImageDTO>> imagesByVariation = imagesProjections.stream()
                .collect(Collectors.groupingBy(
                        ProductVariationImageProjection::getVariationId,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                img -> new ProductVariationImageDTO(
                                        img.getImageId(),
                                        img.getImgUrl(),
                                        img.getPrimary()
                                ),
                                Collectors.toList()
                        )
                ));

        // Cria variações com imagens agrupadas
        List<ProductVariationResponseDTO> variations = variationsProjections.stream()
                .map(v -> new ProductVariationResponseDTO(
                        v.getVariationId(),
                        v.getColor(),
                        v.getSize(),
                        v.getPriceAdjustment(),
                        v.getDiscountAmount(),
                        v.getVariationStock(),
                        imagesByVariation.getOrDefault(v.getVariationId(), List.of())
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

