package com.lcdev.ecommerce.application.dto.payback;

import java.util.List;

public record ReturnCreateDTO(
        Long orderId,
        List<ReturnItemCreateDTO> items
) {}
