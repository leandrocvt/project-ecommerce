package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.payment.PaymentRequest;
import com.lcdev.ecommerce.application.dto.payment.PaymentResponse;
import com.lcdev.ecommerce.application.dto.payment.PaymentStatusResponse;
import com.lcdev.ecommerce.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/{orderId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Criar pagamento para um pedido existente (ex: Pix).
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable Long orderId,
            @RequestParam String gateway,
            @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.processPaymentForOrder(request, orderId, gateway);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Preview de pagamento com cartão, sem salvar o pedido ainda.
     * Pode ficar em outro controller (ex: /payments/preview).
     */
    @PostMapping("/authorize")
    public ResponseEntity<PaymentResponse> previewPayment(
            @RequestParam String gateway,
            @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.authorize(request, gateway);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}/status")
    public ResponseEntity<PaymentStatusResponse> checkStatus(
            @PathVariable String transactionId,
            @RequestParam String gateway) {
        PaymentStatusResponse response = paymentService.checkStatus(transactionId, gateway);
        return ResponseEntity.ok(response);
    }
}
