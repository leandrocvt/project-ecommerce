package com.lcdev.ecommerce.domain.factories;

import com.lcdev.ecommerce.application.service.payment.PaymentProcessor;
import com.lcdev.ecommerce.domain.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProcessorFactory {

    private final List<PaymentProcessor> processors;

    public PaymentProcessor getProcessor(PaymentMethod method) {
        return processors.stream()
                .filter(p -> p.supports(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método de pagamento não suportado: " + method));
    }
}
