package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "Campo obrigatório!")
    private String firstName;

    private String lastName;

    @Email(message = "Informe um email válido!")
    private String email;

    private String phone;
    private LocalDate birthDate;
    private String cpf;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.birthDate = user.getBirthDate();
        this.cpf = user.getCpf();
    }
}
