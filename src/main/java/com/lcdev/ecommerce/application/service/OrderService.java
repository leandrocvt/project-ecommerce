package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.payment.PaymentProcessor;
import com.lcdev.ecommerce.domain.factories.PaymentProcessorFactory;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final AuthService authService;
    private final PaymentProcessorFactory processorFactory;

    @Transactional
    public OrderDTO insert(CreateOrderRequest request) {
        User user = authService.authenticated();
        validateUserPendingOrders(user);

        PaymentProcessor processor = processorFactory.getProcessor(request.paymentMethod());
        return processor.process(request, user);
    }


    private void validateUserPendingOrders(User user) {
        boolean hasPendingOrder = repository.existsByClientAndStatusIn(
                user,
                List.of(OrderStatus.WAITING_PAYMENT)
        );
        if (hasPendingOrder) {
            throw new BadRequestException("Você já possui um pedido em andamento. Aguarde a finalização ou o cancelamento.");
        }
    }

}
