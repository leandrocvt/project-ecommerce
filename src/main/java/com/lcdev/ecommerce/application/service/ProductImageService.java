package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.product.ProductVariationImageDTO;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import com.lcdev.ecommerce.domain.ports.StoragePort;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductVariationImageRepository imageRepository;
    private final ProductVariationRepository productVariationRepository;
    private final StoragePort storagePort;

    public List<ProductVariationImageDTO> uploadImages(Long productId, Long variationId, List<MultipartFile> files) {
        ProductVariation variation = productVariationRepository.findWithProductAndCategoryById(variationId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Variação não pertence ao produto: " + variationId));

        List<ProductVariationImageDTO> dtos = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = generateFileName(file.getOriginalFilename());
            String path = buildPath(variation, filename);

            String url = storagePort.upload(path, file);

            ProductVariationImage image = new ProductVariationImage();
            image.setImgUrl(url);
            image.setPrimary(dtos.isEmpty());
            image.setVariation(variation);

            image = imageRepository.save(image);

            dtos.add(new ProductVariationImageDTO(image.getId(), image.getImgUrl(), image.isPrimary()));
        }

        return dtos;
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ProductVariationImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem não encontrada: " + imageId));

        String path = extractPathFromUrl(image.getImgUrl());
        storagePort.delete(path);

        imageRepository.delete(image);
    }

    private String buildPath(ProductVariation variation, String filename) {
        String productName = variation.getProduct().getName()
                .toLowerCase()
                .replace(" ", "-");

        String color = variation.getColor().name().toLowerCase();
        String size = variation.getSize().name().toLowerCase();

        return String.format("products/%s/%s/%s/%s", productName, color, size, filename);
    }

    private String generateFileName(String originalName) {
        String ext = Optional.ofNullable(originalName)
                .filter(n -> n.contains("."))
                .map(n -> n.substring(originalName.lastIndexOf(".")))
                .orElse("");
        return UUID.randomUUID() + ext;
    }

    private String extractPathFromUrl(String url) {
        return url.substring(url.indexOf("products/"));
    }
}


