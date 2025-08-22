package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import com.lcdev.ecommerce.infrastructure.projections.ProductMinProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductVariationImageRepository extends JpaRepository<ProductVariationImage, Long> {

    @Query("""
    SELECT i.id AS imageId,
           i.imgUrl AS imgUrl,
           i.isPrimary AS primary,
           i.variation.id AS variationId
    FROM ProductVariationImage i
    WHERE i.variation.id IN :variationIds
""")
    List<ProductVariationImageProjection> findImagesByVariationIds(@Param("variationIds") List<Long> variationIds);

}
