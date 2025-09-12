package com.lcdev.ecommerce.application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoWebhookDTO {
    private String type;
    private MercadoPagoData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MercadoPagoData {
        private String id;
    }
}
