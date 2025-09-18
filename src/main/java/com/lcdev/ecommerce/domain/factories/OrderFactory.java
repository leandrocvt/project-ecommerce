package com.lcdev.ecommerce.domain.factories;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.domain.enums.PaymentMethod;
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

        request.items().forEach(itemRequest -> {
            ProductVariation variation = variationMap.get(itemRequest.variationId());
            if (variation == null) {
                throw new ResourceNotFoundException("Variação não encontrada: " + itemRequest.variationId());
            }
            order.addItem(variation, itemRequest.quantity());
        });

        Address address = user.getAddresses().stream()
                .filter(a -> a.getId().equals(request.addressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado para este usuário"));

        order.setShippingRoad(address.getRoad());
        order.setShippingNeighborhood(address.getNeighborhood());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingZipCode(address.getZipCode());
        order.setShippingNumber(address.getNumber());
        order.setShippingComplement(address.getComplement());

        order.setShippingMethod(request.shippingMethod());
        order.setShippingCost(request.shippingCost());
        order.setShippingDeadline(request.shippingDeadline());
        return order;
    }

    private void applyExpirationIfNeeded(Order order, PaymentMethod paymentMethod) {
        if (paymentMethod == null) return;

        if (paymentMethod.equals(PaymentMethod.PIX) || paymentMethod.equals(PaymentMethod.BOLETO)) {
            order.setExpirationMoment(Instant.now().plus(1, ChronoUnit.MINUTES));
        }
    }
}

