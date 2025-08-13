package com.lcdev.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "tb_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String road;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String number;
    private String complement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
