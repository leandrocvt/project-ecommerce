package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {

    @Query("SELECT v FROM ProductVariation v WHERE v.id = :id")
    Optional<ProductVariation> findByIdOnly(@Param("id") Long id);

    @Query("""
    SELECT v
    FROM ProductVariation v
    JOIN FETCH v.product p
    JOIN FETCH p.category c
    WHERE v.id IN :ids
""")
    List<ProductVariation> findAllWithProductAndCategoryByIds(@Param("ids") List<Long> ids);

}
