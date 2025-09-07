package com.lcdev.ecommerce.application.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items,
        String couponCode,
        @NotBlank(message = "Método de envio é obrigatório!")
        String shippingMethod,
        @NotNull(message = "Custo do frete é obrigatório!")
        @DecimalMin(value = "0.00", message = "O custo do frete não pode ser negativo")
        BigDecimal shippingCost,
        @NotNull(message = "Prazo de entrega é obrigatório!")
        @FutureOrPresent(message = "O prazo de entrega deve ser hoje ou uma data futura")
        LocalDate shippingDeadline,
        @NotBlank
        String paymentMethod,
        String cardBrand,
        String token,
        Integer installments,
        String payerIdentificationNumber,
        String payerIdentificationType
) {
}
