package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.address.AddressDTO;
import com.lcdev.ecommerce.domain.entities.Address;
import com.lcdev.ecommerce.domain.entities.User;

public interface AddressMapper {

    Address toEntity(AddressDTO dto, User user);
    AddressDTO toDTO(Address entity);

    void updateEntityFromDto(AddressDTO dto, Address entity);
}
