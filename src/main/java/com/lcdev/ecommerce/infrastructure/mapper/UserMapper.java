package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.UserDTO;
import com.lcdev.ecommerce.application.dto.UserInsertDTO;
import com.lcdev.ecommerce.application.dto.UserUpdateDTO;
import com.lcdev.ecommerce.domain.entities.User;

public interface UserMapper {

    UserDTO mapUserDTO(User user);
    User toEntity(UserInsertDTO dto);
    User mapUpdate(UserUpdateDTO dto, User entity);
}
