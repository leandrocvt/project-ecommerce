package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    @Query("""
                SELECT c
                FROM Coupon c
                WHERE (:code IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :code, '%')))
            """)
    Page<Coupon> search(@Param("code") String code, Pageable pageable);
}
