package com.lcdev.ecommerce.domain.validators;

import com.lcdev.ecommerce.application.service.exceptions.BusinessException;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.OrderItem;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.domain.enums.ReturnStatus;
import com.lcdev.ecommerce.infrastructure.repositories.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReturnValidator {

    private final ReturnRequestRepository returnRepository;

    public void validateOrderEligibility(Order order, User user) {
        if (!order.getClient().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para solicitar devolução deste pedido.");
        }

        if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new BusinessException("Só é possível solicitar devolução de pedidos entregues.");
        }

        if (order.getDeliveredAt() == null) {
            throw new BusinessException("Pedido entregue sem data registrada.");
        }

        Instant deadline = order.getDeliveredAt().plus(7, ChronoUnit.DAYS);
        if (Instant.now().isAfter(deadline)) {
            throw new BusinessException("O prazo de devolução expirou.");
        }

        boolean hasOpenReturn = returnRepository.existsByOrderIdAndStatusIn(
                order.getId(),
                List.of(ReturnStatus.REQUESTED, ReturnStatus.APPROVED, ReturnStatus.RECEIVED)
        );

        if (hasOpenReturn) {
            throw new BusinessException("Já existe uma solicitação de devolução em andamento para este pedido.");
        }
    }

    public void validateItemQuantity(OrderItem orderItem, int requestedQuantity, int alreadyReturned) {
        int availableForReturn = orderItem.getQuantity() - alreadyReturned;
        if (requestedQuantity > availableForReturn) {
            throw new BusinessException(
                    "Quantidade inválida: você só pode devolver até " + availableForReturn + " unidade(s) desse item."
            );
        }
    }
}

