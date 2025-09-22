package com.lcdev.ecommerce.application.dto.order;

import com.lcdev.ecommerce.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateStatus(
        @NotNull(message = "Status é obrigatório") OrderStatus status
) {
}
