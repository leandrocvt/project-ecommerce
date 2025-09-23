package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.message.EmailMessageDTO;
import com.lcdev.ecommerce.application.dto.order.*;
import com.lcdev.ecommerce.application.dto.user.UserUpdateDTO;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.exceptions.BusinessException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.application.service.payment.PaymentProcessor;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.domain.factories.PaymentProcessorFactory;
import com.lcdev.ecommerce.infrastructure.mapper.OrderMapper;
import com.lcdev.ecommerce.infrastructure.messaging.listener.OrderEventListener;
import com.lcdev.ecommerce.infrastructure.messaging.rabbitmq.EmailProducer;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final AuthService authService;
    private final PaymentProcessorFactory processorFactory;
    private final OrderMapper orderMapper;
    private final ProductVariationImageRepository imageRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public OrderDTO insert(CreateOrderRequest request) {
        User user = authService.authenticated();
        validateUserPendingOrders(user);

        PaymentProcessor processor = processorFactory.getProcessor(request.paymentMethod());
        return processor.process(request, user);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> findAll(
            Long id,
            OrderStatus status,
            Instant startDate,
            Instant endDate,
            String clientName,
            Pageable pageable
    ) {
        return repository.search(id, status, startDate, endDate, clientName, pageable)
                .map(OrderResponseDTO::fromProjection);
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
        return findOrderByIdInternal(id, user);
    }

    @Transactional(readOnly = true)
    public OrderDTO findOrderByIdForAdmin(Long id) {
        return findOrderByIdInternal(id, null);
    }

    @Transactional
    public OrderDTO updateStatus(OrderUpdateStatus dto, Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        validateStatusTransition(order.getStatus(), dto.status());

        order.setStatus(dto.status());
        order = repository.save(order);

        if (dto.status() == OrderStatus.DELIVERED) {
            eventPublisher.publishEvent(new OrderDeliveredEvent(order.getId(), order.getClient().getId()));
        }

        return orderMapper.toDTO(order, Map.of());
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {

        if (current == OrderStatus.DELIVERED) {
            throw new ResourceNotFoundException("Não é permitido alterar pedido já entregue");
        }

        if (current.compareTo(OrderStatus.INVOICE_ISSUED) >= 0 && next == OrderStatus.CANCELED) {
            throw new ResourceNotFoundException("Não é permitido cancelar pedido após emissão da nota fiscal");
        }

        if (next.ordinal() < current.ordinal() && next != OrderStatus.CANCELED) {
            throw new ResourceNotFoundException("Transição de status inválida: não é possível retroceder");
        }
    }

    private OrderDTO findOrderByIdInternal(Long id, User client) {
        Order order = (Objects.isNull(client))
                ? repository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"))
                : repository.findByIdAndClientWithDetails(id, client)
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
