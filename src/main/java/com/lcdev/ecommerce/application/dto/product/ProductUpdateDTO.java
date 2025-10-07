package com.lcdev.ecommerce.application.dto.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateDTO(
        @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres!")
        String name,

        @Size(min = 10, message = "Descrição precisa ter no mínimo 10 caracteres!")
        String description,

        @Positive(message = "O preço deve ser positivo!")
        BigDecimal basePrice,

        Long categoryId,

        Boolean active
) {}