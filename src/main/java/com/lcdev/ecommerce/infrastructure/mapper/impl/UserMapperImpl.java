package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.*;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.AddressMapper;
import com.lcdev.ecommerce.infrastructure.mapper.UserMapper;
import com.lcdev.ecommerce.infrastructure.projections.UserMinProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final AddressMapper addressMapper;

    @Override
    public UserResponseDTO mapUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setBirthDate(user.getBirthDate());
        dto.setCpf(user.getCpf());
        user.getRoles().forEach(role -> dto.getRoles().add(new RoleDTO(role)));
        dto.setAddresses(user.getAddresses().stream().map(addressMapper::toDTO).toList());
        return dto;
    }

    @Override
    public UserMinResponseDTO mapUserMinResponseDTO(UserMinProjection projection) {
        return new UserMinResponseDTO(
                projection.getId(),
                projection.getFirstName(),
                projection.getLastName(),
                projection.getEmail()
        );
    }

    @Override
    public User toEntity(UserInsertDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setBirthDate(dto.getBirthDate());
        user.setCpf(dto.getCpf());
        user.setPassword(dto.getPassword());
        return user;
    }

    @Override
    public User mapUpdate(UserUpdateDTO dto, User entity) {
        if (Objects.nonNull(dto.firstName())) entity.setFirstName(dto.firstName());
        if (Objects.nonNull(dto.lastName())) entity.setLastName(dto.lastName());
        if (Objects.nonNull(dto.phone())) entity.setPhone(dto.phone());
        return entity;
    }

}
