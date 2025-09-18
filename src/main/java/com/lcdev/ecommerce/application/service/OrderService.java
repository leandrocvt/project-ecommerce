package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderSummaryDTO;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.application.service.payment.PaymentProcessor;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.factories.PaymentProcessorFactory;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.mapper.impl.OrderMapperImpl;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final AuthService authService;
    private final PaymentProcessorFactory processorFactory;
    private final OrderMapperImpl orderMapper;
    private final ProductVariationImageRepository imageRepository;

    @Transactional
    public OrderDTO insert(CreateOrderRequest request) {
        User user = authService.authenticated();
        validateUserPendingOrders(user);

        PaymentProcessor processor = processorFactory.getProcessor(request.paymentMethod());
        return processor.process(request, user);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDTO> findMyOrders() {
        User user = authService.authenticated();

        return repository.findOrdersSummaryWithImage(user).stream()
                .map(summary -> new OrderSummaryDTO(
                        summary.getId(),
                        summary.getMoment(),
                        summary.getStatus(),
                        summary.getPaymentMethod(),
                        summary.getTotal(),
                        summary.getImgUrl()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO findMyOrderById(Long id) {
        User user = authService.authenticated();
        Order order = repository.findByIdAndClientWithDetails(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        List<Long> variationIds = order.getItems().stream()
                .map(i -> i.getVariation().getId())
                .toList();

        Map<Long, String> primaryImages = loadPrimaryImages(variationIds);

        return orderMapper.toDTO(order, primaryImages);
    }

    private Map<Long, String> loadPrimaryImages(List<Long> variationIds) {
        return imageRepository.findImagesByVariationIds(variationIds)
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getPrimary()))
                .collect(Collectors.toMap(
                        ProductVariationImageProjection::getVariationId,
                        ProductVariationImageProjection::getImgUrl
                ));
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
