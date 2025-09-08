package com.lcdev.ecommerce.domain.factories;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.OrderItem;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderFactory {

    public Order createOrder(CreateOrderRequest request, User user, List<ProductVariation> variations) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setClient(user);

        Map<Long, ProductVariation> variationMap = variations.stream()
                .collect(Collectors.toMap(ProductVariation::getId, Function.identity()));

        applyExpirationIfNeeded(order, request.paymentMethod());

        Set<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    ProductVariation variation = variationMap.get(itemRequest.variationId());
                    if (variation == null) {
                        throw new ResourceNotFoundException("Variação não encontrada: " + itemRequest.variationId());
                    }
                    if (Boolean.FALSE.equals(variation.getProduct().getActive())) {
                        throw new ResourceNotFoundException("Produto inativo: " + variation.getProduct().getId());
                    }
                    return new OrderItem(order, variation, itemRequest.quantity(), variation.getFinalPrice());
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        order.setItems(items);
        order.setShippingMethod(request.shippingMethod());
        order.setShippingCost(request.shippingCost());
        order.setShippingDeadline(request.shippingDeadline());
        return order;
    }

    private void applyExpirationIfNeeded(Order order, String paymentMethod) {
        if (paymentMethod == null) return;

        if (paymentMethod.equalsIgnoreCase("pix") || paymentMethod.equalsIgnoreCase("boleto")) {
            order.setExpirationMoment(Instant.now().plus(30, ChronoUnit.MINUTES));
        }
    }

}
