package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.AddressDTO;
import com.lcdev.ecommerce.domain.entities.Address;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressMapperImpl implements AddressMapper {
    @Override
    public Address toEntity(AddressDTO dto, User user) {
        Address entity = new Address();
        entity.setId(dto.getId());
        entity.setRoad(dto.getRoad());
        entity.setNeighborhood(dto.getNeighborhood());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setZipCode(dto.getZipCode());
        entity.setNumber(dto.getNumber());
        entity.setComplement(dto.getComplement());
        entity.setUser(user);
        return entity;
    }

    @Override
    public AddressDTO toDTO(Address entity) {
        AddressDTO dto = new AddressDTO();
        dto.setId(entity.getId());
        dto.setRoad(entity.getRoad());
        dto.setNeighborhood(entity.getNeighborhood());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setZipCode(entity.getZipCode());
        dto.setNumber(entity.getNumber());
        dto.setComplement(entity.getComplement());
        return dto;
    }

    @Override
    public void updateEntityFromDto(AddressDTO dto, Address entity) {
        entity.setRoad(dto.getRoad());
        entity.setNeighborhood(dto.getNeighborhood());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setZipCode(dto.getZipCode());
        entity.setNumber(dto.getNumber());
        entity.setComplement(dto.getComplement());
    }
}
