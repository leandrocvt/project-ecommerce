package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderItemRequest;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Coupon;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.impl.OrderMapperImpl;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.repositories.CouponRepository;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductVariationImageRepository imageRepository;
    private final ProductVariationRepository variationRepository;
    private final CouponRepository couponRepository;
    private final OrderMapperImpl orderMapper;
    private final OrderFactory orderFactory;
    private final AuthService authService;

    @Transactional
    public OrderDTO insert(CreateOrderRequest request) {
        User user = authService.authenticated();

        List<ProductVariation> variations = fetchVariations(request);

        Order order = orderFactory.createOrder(request, user, variations);

        applyCouponIfPresent(order, request.couponCode());

        repository.save(order);

        Map<Long, String> primaryImages = fetchPrimaryImages(variations);

        return orderMapper.toDTO(order, primaryImages);
    }

    private List<ProductVariation> fetchVariations(CreateOrderRequest request) {
        List<Long> variationIds = request.items().stream()
                .map(OrderItemRequest::variationId)
                .toList();

        return variationRepository.findAllWithProductAndCategoryByIds(variationIds);
    }

    private void applyCouponIfPresent(Order order, String couponCode) {
        if (Objects.nonNull(couponCode)) {
            Coupon coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado"));
            order.applyCoupon(coupon);
        }
    }

    private Map<Long, String> fetchPrimaryImages(List<ProductVariation> variations) {
        List<Long> variationIds = variations.stream()
                .map(ProductVariation::getId)
                .toList();

        return imageRepository.findImagesByVariationIds(variationIds)
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getPrimary()))
                .collect(Collectors.toMap(
                        ProductVariationImageProjection::getVariationId,
                        ProductVariationImageProjection::getImgUrl
                ));
    }

}
