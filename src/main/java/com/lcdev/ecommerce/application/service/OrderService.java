package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.OrderDTO;
import com.lcdev.ecommerce.application.dto.OrderItemRequest;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.infrastructure.mapper.impl.OrderMapperImpl;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.repositories.CouponRepository;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductVariationImageRepository imageRepository;
    private final ProductVariationRepository variationRepository;
    private final CouponRepository couponRepository;
    private final OrderMapperImpl orderMapper;
    private final AuthService authService;


    @Transactional
    public OrderDTO insert(CreateOrderRequest request) {
        User user = authService.authenticated();

        List<Long> variationIds = request.items().stream()
                .map(OrderItemRequest::variationId)
                .toList();

        List<ProductVariation> variations = variationRepository
                .findAllWithProductAndCategoryByIds(variationIds);

        Order order = orderMapper.toEntity(request, user, variations);

        BigDecimal subtotal = order.getItems().stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.couponCode() != null) {
            Coupon coupon = couponRepository.findByCode(request.couponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado"));

            BigDecimal discount = coupon.calculateDiscount(subtotal).setScale(2, RoundingMode.HALF_UP);
            order.setCoupon(coupon);
            order.setDiscountApplied(discount);
            coupon.incrementUsage();
        }

        repository.save(order);

        Map<Long, String> primaryImages = imageRepository.findImagesByVariationIds(variationIds)
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getPrimary()))
                .collect(Collectors.toMap(
                        ProductVariationImageProjection::getVariationId,
                        ProductVariationImageProjection::getImgUrl
                ));

        return orderMapper.toDTO(order, primaryImages);
    }

}
