package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientDTO {

    private Long id;
    private String name;

    public ClientDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ClientDTO(User entity) {
        if (entity != null) {
            id = entity.getId();
            name = entity.getFirstName().concat(" ").concat(entity.getLastName());
        }
    }

}
