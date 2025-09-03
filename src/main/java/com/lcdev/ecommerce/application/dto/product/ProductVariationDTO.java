package com.lcdev.ecommerce.application.dto.product;

import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.enums.Color;
import com.lcdev.ecommerce.domain.enums.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationDTO {

    private Long id;

    @NotNull(message = "A cor da variação é obrigatória.")
    private Color color;

    @NotNull(message = "O tamanho da variação é obrigatório.")
    private Size size;

    @PositiveOrZero(message = "O ajuste de preço não pode ser negativo.")
    private BigDecimal priceAdjustment;

    @PositiveOrZero(message = "O desconto não pode ser negativo.")
    private BigDecimal discountAmount;

    @NotNull(message = "Quantidade em estoque é obrigatória.")
    @PositiveOrZero(message = "Estoque não pode ser negativo.")
    private Integer stockQuantity;

    private List<ProductVariationImageDTO> images;

    public ProductVariationDTO(ProductVariation variation) {
        id = variation.getId();
        color = variation.getColor();
        size = variation.getSize();
        priceAdjustment = variation.getPriceAdjustment();
        discountAmount = variation.getDiscountAmount();
        stockQuantity = variation.getStockQuantity();
        if (Objects.nonNull(variation.getImages())) {
            images = variation.getImages().stream()
                    .map(img -> new ProductVariationImageDTO(img.getId(), img.getImgUrl(), img.isPrimary()))
                    .toList();
        }
    }

}
