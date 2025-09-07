package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderItemRequest;
import com.lcdev.ecommerce.application.dto.payment.PaymentRequest;
import com.lcdev.ecommerce.application.dto.payment.PaymentResponse;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Coupon;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.PaymentStatus;
import com.lcdev.ecommerce.domain.factories.OrderFactory;
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
    private final PaymentService paymentService;

    @Transactional
    public OrderDTO insert(CreateOrderRequest request) {
        return switch (request.paymentMethod().toLowerCase()) {
            case "pix" -> createOrderWithPix(request);
            case "card" -> createOrderWithCard(request);
            default -> throw new IllegalArgumentException("Método de pagamento não suportado: " + request.paymentMethod());
        };
    }

    private OrderDTO createOrderWithPix(CreateOrderRequest request) {
        User user = authService.authenticated();
        List<ProductVariation> variations = fetchVariations(request);

        Order order = orderFactory.createOrder(request, user, variations);
        applyCouponIfPresent(order, request.couponCode());

        repository.save(order);

        Map<Long, String> primaryImages = fetchPrimaryImages(variations);
        return orderMapper.toDTO(order, primaryImages);
    }

    private OrderDTO createOrderWithCard(CreateOrderRequest request) {
        User user = authService.authenticated();
        List<ProductVariation> variations = fetchVariations(request);

        Order order = orderFactory.createOrder(request, user, variations);
        applyCouponIfPresent(order, request.couponCode());

        BigDecimal total = order.getTotal();

        // 1. Autoriza no gateway (ainda não cria pedido no DB)
        PaymentResponse authResponse = paymentService.authorize(
                new PaymentRequest(
                        total,
                        "BRL",
                        request.paymentMethod(),   // ex: "visa"
                        request.token(),
                        request.cardBrand(),
                        request.installments(),
                        user.getEmail(),
                        request.payerIdentificationType(),
                        request.payerIdentificationNumber()
                ),
                "mercadopago"
        );

        if (authResponse.status() == PaymentStatus.FAILED) {
            throw new BadRequestException("Pagamento recusado pelo cartão.");
        }

        // 2. Agora sim salva o pedido
        repository.save(order);

        // 3. Captura o pagamento no banco, vinculando ao pedido
        paymentService.capture(authResponse, order, "mercadopago", "card", request.cardBrand());

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
