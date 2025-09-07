package com.lcdev.ecommerce.domain.factories;

import com.lcdev.ecommerce.infrastructure.payment.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final List<PaymentGateway> gateways;

    public PaymentGateway getGateway(String gatewayName) {
        return gateways.stream()
                .filter(g -> g.getClass().getSimpleName().toLowerCase().contains(gatewayName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gateway não suportado: " + gatewayName));
    }

}