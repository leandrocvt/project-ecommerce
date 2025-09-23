package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.product.ProductMinResponseDTO;
import com.lcdev.ecommerce.application.dto.product.ProductResponseDTO;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.infrastructure.projections.ProductMinProjection;
import com.lcdev.ecommerce.infrastructure.repositories.FavoriteProductRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
import com.lcdev.ecommerce.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {

    private final FavoriteProductRepository repository;
    private final ProductVariationRepository variationRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Transactional
    public void addFavorite(Long variationId) {
        User user = getCurrentUser();
        ProductVariation variation = variationRepository.findById(variationId)
                .orElseThrow(() -> new ResourceNotFoundException("Variação não encontrada"));

        FavoriteProductPK pk = new FavoriteProductPK(variation.getId(), user.getId());
        if (repository.existsById(pk)) {
            return;
        }

        FavoriteProduct favorite = new FavoriteProduct(user, variation);
        repository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long productId) {
        User user = getCurrentUser();
        FavoriteProductPK pk = new FavoriteProductPK(productId, user.getId());
        repository.deleteById(pk);
    }

    @Transactional(readOnly = true)
    public List<ProductMinResponseDTO> listFavorites() {
        User user = getCurrentUser();
        List<ProductMinProjection> favorites = repository.findFavoriteProductsByUserId(user.getId());
        return favorites.stream()
                .map(ProductMinResponseDTO::from)
                .toList();
    }

    private User getCurrentUser() {
        return authService.authenticated(userRepository::findByEmailOptional);
    }

}
