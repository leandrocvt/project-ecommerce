package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres!")
    @NotBlank(message = "Campo requerido!")
    private String name;

    @Size(min = 10, message = "Descrição precisa ter no mínimo 10 caracteres!")
    @NotBlank(message = "Campo requerido!")
    private String description;

    @NotNull(message = "Campo requerido.")
    @Positive(message = "O preço deve ser positivo!")
    private BigDecimal basePrice;

    @NotNull(message = "Categoria requerida!")
    private Long categoryId;

    private List<ProductVariationDTO> variations;

}
