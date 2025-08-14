package com.lcdev.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String password;
    private String cpf;

    @OneToMany(mappedBy = "client")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private Set<Address> addresses = new HashSet<>();

}
