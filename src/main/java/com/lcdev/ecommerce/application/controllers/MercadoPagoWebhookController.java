package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.payment.MercadoPagoWebhookDTO;
import com.lcdev.ecommerce.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody MercadoPagoWebhookDTO payload) {
        try {
            if ("payment".equals(payload.getType())) {
                String paymentId = payload.getData().getId();
                paymentService.updateStatusFromGateway(paymentId, "mercadopago");
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}