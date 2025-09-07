package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
