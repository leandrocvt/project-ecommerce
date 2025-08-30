package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.*;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.projections.UserMinProjection;

public interface UserMapper {

    UserResponseDTO mapUserResponseDTO(User user);
    User toEntity(UserInsertDTO dto);
    User mapUpdate(UserUpdateDTO dto, User entity);

    UserMinResponseDTO mapUserMinResponseDTO(UserMinProjection projection);


}
