package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.product.*;
import com.lcdev.ecommerce.application.service.exceptions.BusinessException;
import com.lcdev.ecommerce.application.service.exceptions.InactiveProductException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.enums.Size;
import com.lcdev.ecommerce.domain.factories.ProductFactory;
import com.lcdev.ecommerce.infrastructure.mapper.ProductMapper;
import com.lcdev.ecommerce.infrastructure.mapper.ProductVariationMapper;
import com.lcdev.ecommerce.infrastructure.projections.AssessmentProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationProjection;
import com.lcdev.ecommerce.infrastructure.projections.ReviewSummaryProjection;
import com.lcdev.ecommerce.infrastructure.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductVariationImageRepository imageRepository;
    private final ProductVariationRepository variationRepository;
    private final ProductVariationMapper productVariationMapper;
    private final CategoryRepository categoryRepository;
    private final AssessmentRepository assessmentRepository;
    private final ProductMapper productMapper;
    private final ProductFactory productFactory;

    @Transactional
    public ProductResponseDTO save(ProductRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));

        Product entity = productFactory.createFromDTO(dto, category);
        validateVariations(entity);

        entity = repository.save(entity);
        return productMapper.toResponseDTO(entity);
    }

    @Transactional
    public ProductResponseDTO updateBasicInfo(Long id, ProductUpdateDTO dto) {
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));

        productMapper.updateBasicFields(dto, entity);

        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + dto.categoryId()));
            entity.setCategory(category);
        }

        return productMapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public ProductVariationResponseDTO updateVariation(Long productId, Long variationId, ProductVariationUpdateDTO dto) {
        ProductVariation variation = variationRepository.findByIdAndProductId(variationId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Variação não encontrada"));

        productVariationMapper.updateEntity(dto, variation);
        return ProductVariationResponseDTO.from(variationRepository.save(variation));
    }

    @Transactional
    public ProductVariationResponseDTO addVariation(Long productId, ProductVariationRequestDTO dto) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        ProductVariation variation = productVariationMapper.toEntity(dto, product);
        return ProductVariationResponseDTO.from(variationRepository.save(variation));
    }

    @Transactional
    public void deleteVariation(Long productId, Long variationId) {
        ProductVariation variation = variationRepository.findByIdAndProductId(variationId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Variação não encontrada"));
        variationRepository.delete(variation);
    }


    @Transactional(readOnly = true)
    public Page<ProductMinResponseDTO> search(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Size size,
            String sort,
            Pageable pageable) {

        String sizeParam = size != null ? size.name() : null;
        return repository.search(name, categoryId, minPrice, maxPrice, sizeParam, sort, pageable)
                .map(ProductMinResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public ProductDetailsResponseDTO findById(Long productId) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (Boolean.FALSE.equals(product.getActive())) {
            throw new InactiveProductException("Produto está inativo");
        }

        List<ProductVariationProjection> variations = repository.findProductWithVariations(productId);
        List<Long> variationIds = variations.stream().map(ProductVariationProjection::getVariationId).toList();

        List<ProductVariationImageProjection> images = variationIds.isEmpty()
                ? List.of()
                : imageRepository.findImagesByVariationIds(variationIds);

        ReviewSummaryProjection summary = assessmentRepository.findReviewSummaryByProductId(productId);

        List<AssessmentProjection> sample = assessmentRepository.findByProductId(
                productId, PageRequest.of(0, 3)).getContent();

        Integer totalStock = repository.findTotalStockByProductId(productId);

        ProductDetailsResponseDTO dto = productMapper.toDetailsDTO(variations, images, summary, sample);
        dto.setStockQuantity(totalStock);

        return dto;
    }

    private void validateVariations(Product product) {
        product.getVariations().forEach(variation -> {
            BigDecimal variantPrice = variation.getVariantPrice();
            BigDecimal discount = variation.getDiscountAmount() != null ? variation.getDiscountAmount() : BigDecimal.ZERO;

            if (discount.compareTo(variantPrice) > 0) {
                throw new BusinessException(
                        "O desconto da variação (" + discount +
                                ") não pode ser maior que o preço da variação (" + variantPrice + ")."
                );
            }
        });
    }
}
