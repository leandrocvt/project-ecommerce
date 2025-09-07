package com.lcdev.ecommerce.application.config;


import com.lcdev.ecommerce.infrastructure.payment.PaymentGateway;
import com.lcdev.ecommerce.infrastructure.payment.mercadopago.MercadoPagoClient;
import com.lcdev.ecommerce.infrastructure.payment.mercadopago.MercadoPagoGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    public MercadoPagoClient mercadoPagoClient(@Value("${mercadopago.access-token}") String accessToken) {
        return new MercadoPagoClient(accessToken);
    }

    @Bean
    public PaymentGateway mercadoPagoGateway(MercadoPagoClient client) {
        return new MercadoPagoGateway(client);
    }

}
