package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.enums.Color;
import com.lcdev.ecommerce.domain.enums.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationDTO {

    private Long id;

    @NotNull(message = "A cor da variação é obrigatória.")
    private Color color;

    @NotNull(message = "O tamanho da variação é obrigatório.")
    private Size size;

    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser positivo.")
    private BigDecimal priceAdjustment;

    @NotNull(message = "Quantidade em estoque é obrigatória.")
    @PositiveOrZero(message = "Estoque não pode ser negativo.")
    private Integer stockQuantity;

    private List<String> imgUrls;

    public ProductVariationDTO(ProductVariation variation) {
        id = variation.getId();
        color = variation.getColor();
        size = variation.getSize();
        priceAdjustment = variation.getPriceAdjustment();
        stockQuantity = variation.getStockQuantity();
        imgUrls = variation.getImgUrls();
    }

}
