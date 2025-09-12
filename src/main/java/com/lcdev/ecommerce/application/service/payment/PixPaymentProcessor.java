package com.lcdev.ecommerce.application.service.payment;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderItemRequest;
import com.lcdev.ecommerce.application.dto.payment.PaymentRequest;
import com.lcdev.ecommerce.application.dto.payment.PaymentResponse;
import com.lcdev.ecommerce.application.service.PaymentService;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Coupon;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.PaymentMethod;
import com.lcdev.ecommerce.domain.factories.OrderFactory;
import com.lcdev.ecommerce.infrastructure.mapper.impl.OrderMapperImpl;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.repositories.CouponRepository;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PixPaymentProcessor implements PaymentProcessor {

    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final CouponRepository couponRepository;
    private final ProductVariationRepository variationRepository;
    private final ProductVariationImageRepository imageRepository;
    private final OrderMapperImpl orderMapper;
    private final PaymentService paymentService;

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.PIX;
    }

    @Override
    @Transactional
    public OrderDTO process(CreateOrderRequest request, User user) {
        List<ProductVariation> variations = variationRepository.findAllWithProductAndCategoryByIds(
                request.items().stream().map(OrderItemRequest::variationId).toList()
        );

        Order order = orderFactory.createOrder(request, user, variations);
        applyCouponIfPresent(order, request.couponCode());
        orderRepository.save(order);

        PaymentResponse response = paymentService.processPaymentForOrder(
                new PaymentRequest(
                        order.getTotal(),
                        "BRL",
                        "pix",
                        null,
                        null,
                        null,
                        user.getEmail(),
                        request.payerIdentificationType(),
                        request.payerIdentificationNumber()
                ),
                order.getId(),
                "mercadopago"
        );

        Map<Long, String> primaryImages = fetchPrimaryImages(variations);
        OrderDTO dto = orderMapper.toDTO(order, primaryImages);
        dto.setPixQrCode(response.pixCode());
        return dto;
    }

    private void applyCouponIfPresent(Order order, String couponCode) {
        if (couponCode != null) {
            Coupon coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado"));
            order.applyCoupon(coupon);
        }
    }

    private Map<Long, String> fetchPrimaryImages(List<ProductVariation> variations) {
        return imageRepository.findImagesByVariationIds(
                        variations.stream().map(ProductVariation::getId).toList()
                )
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getPrimary()))
                .collect(Collectors.toMap(
                        ProductVariationImageProjection::getVariationId,
                        ProductVariationImageProjection::getImgUrl
                ));
    }
}

