package com.lcdev.ecommerce.application.dto.address;

public record ShippingAddressDTO(
        String road,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String number,
        String complement
) {
}
