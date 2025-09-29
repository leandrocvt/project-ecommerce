package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.payback.ReturnCreateDTO;
import com.lcdev.ecommerce.application.dto.payback.ReturnItemCreateDTO;
import com.lcdev.ecommerce.application.dto.payback.ReturnResponseDTO;
import com.lcdev.ecommerce.application.dto.payback.ReturnSummaryDTO;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.domain.enums.ReturnStatus;
import com.lcdev.ecommerce.domain.factories.ReturnFactory;
import com.lcdev.ecommerce.domain.validators.ReturnValidator;
import com.lcdev.ecommerce.infrastructure.mapper.ReturnMapper;
import com.lcdev.ecommerce.infrastructure.repositories.OrderItemRepository;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ReturnRequestRepository;
import com.lcdev.ecommerce.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRequestRepository repository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ReturnMapper mapper;
    private final ReturnValidator validator;
    private final ReturnFactory factory;

    @Transactional
    public ReturnResponseDTO requestReturn(ReturnCreateDTO dto) {
        User user = getCurrentUser();
        Order order = fetchOrder(dto.orderId());
        validator.validateOrderEligibility(order, user);

        ReturnRequest request = createReturnRequest(dto, order, user);

        repository.save(request);

        ReturnRequest loaded = repository.findByIdAndUserIdWithDetails(request.getId(), user.getId())
                .orElse(request);

        return mapper.toResponse(loaded);
    }

    @Transactional
    public ReturnResponseDTO updateStatus(Long id, ReturnStatus status) {
        ReturnRequest request = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolução não encontrada"));

        request.setStatus(status);
        request.getItems().forEach(item -> item.setStatus(status));
        repository.save(request);

        return mapper.toResponse(request);
    }

    @Transactional(readOnly = true)
    public List<ReturnResponseDTO> findByOrderId(Long orderId) {
        User user = getCurrentUser();
        List<ReturnRequest> requests = repository.findByOrderIdAndUserIdWithDetails(orderId, user.getId());
        return requests.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ReturnResponseDTO> findById(Long id) {
        User user = getCurrentUser();
        return repository.findByIdAndUserIdWithDetails(id, user.getId())
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReturnSummaryDTO> findAll(
            Long id,
            ReturnStatus status,
            Instant startDate,
            Instant endDate,
            String clientName,
            Pageable pageable
    ) {
        return repository.search(id, status, startDate, endDate, clientName, pageable)
                .map(ReturnSummaryDTO::fromProjection);
    }

    private Order fetchOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }

    private ReturnRequest createReturnRequest(ReturnCreateDTO dto, Order order, User user) {
        Instant deadline = order.getDeliveredAt().plus(7, ChronoUnit.DAYS);
        ReturnRequest request = factory.createRequest(order, user, deadline);

        Map<Long, OrderItem> orderItemMap = fetchOrderItems(order.getId(), dto.items());

        dto.items().forEach(itemDto -> {
            OrderItem orderItem = orderItemMap.get(itemDto.variationId());
            if (orderItem == null) throw new ResourceNotFoundException("Item do pedido não encontrado");

            int alreadyReturned = repository.sumReturnedQuantity(
                    order.getId(),
                    orderItem.getVariation().getId(),
                    ReturnStatus.validForCounting()
            );

            validator.validateItemQuantity(orderItem, itemDto.quantity(), alreadyReturned);

            ReturnItem item = factory.createItem(request, orderItem, itemDto);
            request.getItems().add(item);
        });

        return request;
    }

    private Map<Long, OrderItem> fetchOrderItems(Long orderId, List<ReturnItemCreateDTO> items) {
        List<Long> variationIds = items.stream()
                .map(ReturnItemCreateDTO::variationId)
                .toList();

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderIdAndVariationIds(orderId, variationIds);

        return orderItems.stream()
                .collect(Collectors.toMap(oi -> oi.getVariation().getId(), oi -> oi));
    }

    private User getCurrentUser() {
        return authService.authenticated(userRepository::findByEmailOptional);
    }
}

