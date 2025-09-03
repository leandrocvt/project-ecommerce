package com.lcdev.ecommerce.application.dto.user;

import com.lcdev.ecommerce.application.dto.address.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String cpf;
    private Set<RoleDTO> roles = new HashSet<>();
    private List<AddressDTO> addresses = new ArrayList<>();
}
