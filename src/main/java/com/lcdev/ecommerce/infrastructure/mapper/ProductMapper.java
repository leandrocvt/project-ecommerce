package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.product.ProductDetailsResponseDTO;
import com.lcdev.ecommerce.application.dto.product.ProductRequestDTO;
import com.lcdev.ecommerce.application.dto.product.ProductResponseDTO;
import com.lcdev.ecommerce.application.dto.product.ProductUpdateDTO;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.infrastructure.projections.AssessmentProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationProjection;
import com.lcdev.ecommerce.infrastructure.projections.ReviewSummaryProjection;

import java.util.List;

public interface ProductMapper {

    ProductResponseDTO toResponseDTO(Product entity);
    void updateBasicFields(ProductUpdateDTO dto, Product entity);

    ProductDetailsResponseDTO toDetailsDTO(
            List<ProductVariationProjection> variationsProjections,
            List<ProductVariationImageProjection> imagesProjections,
            ReviewSummaryProjection reviewSummary,
            List<AssessmentProjection> sampleReviews);
}
