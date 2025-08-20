package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.ProductMinResponse;
import com.lcdev.ecommerce.application.dto.ProductRequestDTO;
import com.lcdev.ecommerce.application.dto.ProductResponseDTO;
import com.lcdev.ecommerce.application.service.exceptions.BusinessException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.enums.Size;
import com.lcdev.ecommerce.infrastructure.mapper.ProductMapper;
import com.lcdev.ecommerce.infrastructure.mapper.ProductVariationMapper;
import com.lcdev.ecommerce.infrastructure.repositories.CategoryRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper productMapper;
    private final ProductVariationMapper productVariationMapper;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponseDTO save(ProductRequestDTO dto){

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));

        Product entity = productMapper.toEntity(dto, category);
        validateVariations(entity);
        entity = repository.save(entity);
        return productMapper.toResponseDTO(entity);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto){
        try {
            Product entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Id not found! Id: " + id));

            productMapper.updateBasicFields(dto, entity);

            if (Objects.nonNull(dto.getCategoryId())) {
                Category category = categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found. Id: " + dto.getCategoryId()));
                entity.setCategory(category);
            }

            productVariationMapper.updateVariations(dto, entity);

            entity = repository.save(entity);
            return productMapper.toResponseDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id not found! Id:" + id);
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductMinResponse> search(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Size size,
            String sort,
            Pageable pageable) {

        String sizeParam = size != null ? size.name() : null;
        return repository.search(name, categoryId, minPrice, maxPrice, sizeParam, sort, pageable)
                .map(ProductMinResponse::from);
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
