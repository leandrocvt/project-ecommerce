package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.payment.PaymentRequest;
import com.lcdev.ecommerce.application.dto.payment.PaymentResponse;
import com.lcdev.ecommerce.application.dto.payment.PaymentStatusResponse;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.Payment;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.domain.enums.PaymentStatus;
import com.lcdev.ecommerce.domain.factories.PaymentGatewayFactory;
import com.lcdev.ecommerce.infrastructure.payment.PaymentGateway;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final AuthService authService;

    /**
     * Fluxo Pix/Boleto: já existe pedido → cria pagamento associado ao pedido.
     */
    @Transactional
    public PaymentResponse processPaymentForOrder(PaymentRequest request, Long orderId, String gatewayName) {
        User user = authService.authenticated();
        PaymentRequest enrichedRequest = enrichRequestWithUser(request, user);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        PaymentGateway gateway = gatewayFactory.getGateway(gatewayName);
        PaymentResponse response = gateway.create(order, enrichedRequest);

        capture(response, order, gatewayName, enrichedRequest.paymentMethod(), enrichedRequest.cardBrand());

        return response;
    }

    /**
     * Fluxo Cartão: apenas autoriza com o gateway.
     * Não cria registro em banco ainda.
     */
    @Transactional(readOnly = true)
    public PaymentResponse authorize(PaymentRequest request, String gatewayName) {
        User user = authService.authenticated();
        PaymentRequest enrichedRequest = enrichRequestWithUser(request, user);

        PaymentGateway gateway = gatewayFactory.getGateway(gatewayName);
        return gateway.authorize(enrichedRequest);
    }

    @Transactional
    public void updateStatusFromGateway(String transactionId, String gatewayName) {
        // consulta o status atualizado direto no gateway
        PaymentGateway gateway = gatewayFactory.getGateway(gatewayName);
        PaymentStatusResponse statusResponse = gateway.checkStatus(transactionId);

        // carrega o pagamento pelo transactionId
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + transactionId));

        // atualiza status do pagamento
        payment.setStatus(statusResponse.status());
        payment.setMoment(Instant.now());
        paymentRepository.save(payment);

        // atualiza o pedido associado
        Order order = payment.getOrder();
        if (statusResponse.status() == PaymentStatus.PAID) {
            order.setStatus(OrderStatus.PAID);
        } else if (statusResponse.status() == PaymentStatus.FAILED) {
            order.setStatus(OrderStatus.CANCELED);
        }
        orderRepository.save(order);
    }

    /**
     * Consulta status direto no gateway.
     */
    @Transactional(readOnly = true)
    public PaymentStatusResponse checkStatus(String transactionId, String gatewayName) {
        PaymentGateway gateway = gatewayFactory.getGateway(gatewayName);
        return gateway.checkStatus(transactionId);
    }

    @Transactional
    public void capture(PaymentResponse response, Order order, String gatewayName, String method, String cardBrand) {
        Payment payment = order.getPayment();

        if (payment == null) {
            payment = new Payment();
            payment.setOrder(order);
        }

        payment.setMoment(Instant.now());
        payment.setStatus(response.status());
        payment.setTransactionId(response.transactionId());
        payment.setGateway(gatewayName);
        payment.setPaymentMethod(method);

        if ("card".equalsIgnoreCase(method)) {
            payment.setCardBrand(cardBrand);
        }

        paymentRepository.save(payment);
        order.setPayment(payment);
    }

    private PaymentRequest enrichRequestWithUser(PaymentRequest request, User user) {
        return new PaymentRequest(
                request.amount(),
                request.currency(),
                request.paymentMethod(),
                request.token(),
                request.cardBrand(),
                request.installments(),
                request.payerEmail() != null ? request.payerEmail() : user.getEmail(),
                request.payerIdentificationType(),
                request.payerIdentificationNumber()
        );
    }

}
