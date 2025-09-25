package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Address;
import com.lcdev.ecommerce.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdAndUser_Id(Long id, Long userId);

    @Query("""
                SELECT a FROM Address a
                WHERE a.user = :user
                  AND a.zipCode = :zipCode
                  AND a.number = :number
            """)
    Optional<Address> findDuplicateByZipCodeAndNumber(@Param("user") User user,
                                                      @Param("zipCode") String zipCode,
                                                      @Param("number") String number);
}

