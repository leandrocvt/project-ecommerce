package com.lcdev.ecommerce.application.dto.product;

import com.lcdev.ecommerce.domain.enums.Color;
import com.lcdev.ecommerce.domain.enums.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariationRequestDTO {
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

}
