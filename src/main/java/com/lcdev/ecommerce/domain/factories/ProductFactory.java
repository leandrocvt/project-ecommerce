package com.lcdev.ecommerce.domain.factories;

import com.lcdev.ecommerce.application.dto.product.ProductRequestDTO;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.infrastructure.mapper.ProductVariationMapper;
import com.lcdev.ecommerce.infrastructure.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFactory {

    private final ProductVariationMapper productVariationMapper;

    public Product createFromDTO(ProductRequestDTO dto, Category category) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setBasePrice(dto.getBasePrice());
        product.setCategory(category);
        product.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);

        productVariationMapper.applyVariationsFromDTO(dto, product);
        return product;
    }

}


