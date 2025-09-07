package com.lcdev.ecommerce.infrastructure.payment.mercadopago;

import com.lcdev.ecommerce.application.dto.payment.*;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.enums.PaymentStatus;
import com.lcdev.ecommerce.infrastructure.payment.PaymentGateway;

public class MercadoPagoGateway implements PaymentGateway {

    private final MercadoPagoClient mercadoPagoClient;

    public MercadoPagoGateway(MercadoPagoClient mercadoPagoClient) {
        this.mercadoPagoClient = mercadoPagoClient;
    }

    @Override
    public PaymentResponse authorize(PaymentRequest request) {
        MercadoPagoCreatePaymentResponse response =
                mercadoPagoClient.createPayment(request, null);
        return mapToResponse(request, response);
    }

    @Override
    public PaymentResponse create(Order order, PaymentRequest request) {
        MercadoPagoCreatePaymentResponse response =
                mercadoPagoClient.createPayment(request, order);
        return mapToResponse(request, response);
    }

    @Override
    public PaymentStatusResponse checkStatus(String transactionId) {
        MercadoPagoPaymentStatusResponse response =
                mercadoPagoClient.getPaymentStatus(transactionId);

        return new PaymentStatusResponse(
                response.id(),
                mapStatus(response.status())
        );
    }

    private PaymentResponse mapToResponse(PaymentRequest request, MercadoPagoCreatePaymentResponse response) {
        String redirectUrl = null;
        String pixCode = null;

        if ("pix".equalsIgnoreCase(request.paymentMethod())) {
            pixCode = response.redirectUrl();
        } else {
            redirectUrl = response.redirectUrl();
        }

        return new PaymentResponse(
                response.id(),
                mapStatus(response.status()),
                redirectUrl,
                pixCode
        );
    }

    private PaymentStatus mapStatus(String status) {
        return switch (status.toLowerCase()) {
            case "approved" -> PaymentStatus.PAID;
            case "in_process" -> PaymentStatus.PENDING;
            case "rejected" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING;
        };
    }
}