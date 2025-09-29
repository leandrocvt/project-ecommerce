package com.lcdev.ecommerce.domain.enums;

import java.util.Collection;
import java.util.List;

public enum ReturnStatus {
    REQUESTED,
    APPROVED,
    RECEIVED,
    REFUNDED,
    REJECTED;

    public static Collection<ReturnStatus> validForCounting() {
        return List.of(REQUESTED, APPROVED, RECEIVED, REFUNDED);
    }
}