package com.lcdev.ecommerce.infrastructure.payment.mercadopago;

import com.lcdev.ecommerce.application.dto.payment.MercadoPagoCreatePaymentResponse;
import com.lcdev.ecommerce.application.dto.payment.MercadoPagoPaymentStatusResponse;
import com.lcdev.ecommerce.application.dto.payment.PaymentRequest;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.infrastructure.payment.mercadopago.exceptions.MercadoPagoException;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MercadoPagoClient {

    private final PaymentClient paymentClient;

    public MercadoPagoClient(String accessToken) {
        MercadoPagoConfig.setAccessToken(accessToken);
        this.paymentClient = new PaymentClient();
    }

    private MercadoPagoException toMercadoPagoException(MPApiException e) {
        String content = e.getApiResponse().getContent();
        int status = e.getApiResponse().getStatusCode();

        return new MercadoPagoException(
                "Erro do MercadoPago",
                status,
                content
        );
    }

    public MercadoPagoCreatePaymentResponse createPayment(PaymentRequest request, Order order) {
        try {
            PaymentCreateRequest createRequest = buildPaymentRequest(request, order);
            Payment payment = paymentClient.create(createRequest);

            String redirectOrPixCode = extractRedirectOrPixCode(request, payment);

            return new MercadoPagoCreatePaymentResponse(
                    payment.getId().toString(),
                    payment.getStatus(),
                    redirectOrPixCode
            );

        } catch (MPApiException e) {
            throw toMercadoPagoException(e);
        } catch (MPException e) {
            throw new MercadoPagoException("Falha genérica no SDK do MercadoPago", 500, e.getMessage());
        }
    }

    public MercadoPagoPaymentStatusResponse getPaymentStatus(String transactionId) {
        try {
            Payment payment = paymentClient.get(Long.valueOf(transactionId));

            return new MercadoPagoPaymentStatusResponse(
                    payment.getId().toString(),
                    payment.getStatus()
            );

        } catch (MPApiException e) {
            throw toMercadoPagoException(e);
        } catch (MPException e) {
            throw new MercadoPagoException("Falha ao consultar status no MercadoPago", 500, e.getMessage());
        }
    }

    private PaymentCreateRequest buildPaymentRequest(PaymentRequest request, Order order) {
        String paymentMethodId = switch (request.paymentMethod().toLowerCase()) {
            case "pix" -> "pix";
            case "card" -> request.cardBrand();
            default -> request.paymentMethod();
        };

        PaymentCreateRequest.PaymentCreateRequestBuilder builder = PaymentCreateRequest.builder()
                .transactionAmount(request.amount())
                .description("Pagamento do pedido " + (order != null ? order.getId() : "preview"))
                .paymentMethodId(paymentMethodId)
                .payer(buildPayer(request));

        if (request.token() != null) builder.token(request.token());
        if (request.installments() != null) builder.installments(request.installments());

        return builder.build();
    }

    private PaymentPayerRequest buildPayer(PaymentRequest request) {
        return PaymentPayerRequest.builder()
                .email(request.payerEmail())
                .firstName("Cliente") // pode vir do User futuramente
                .identification(
                        IdentificationRequest.builder()
                                .type(request.payerIdentificationType())
                                .number(request.payerIdentificationNumber())
                                .build()
                )
                .build();
    }

    private String extractRedirectOrPixCode(PaymentRequest request, Payment payment) {
        return switch (request.paymentMethod().toLowerCase()) {
            case "pix" -> payment.getPointOfInteraction().getTransactionData().getQrCode();
            case "card" -> payment.getTransactionDetails().getExternalResourceUrl();
            default -> null;
        };
    }

}

