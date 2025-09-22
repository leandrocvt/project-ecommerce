package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.FavoriteProduct;
import com.lcdev.ecommerce.domain.entities.FavoriteProductPK;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.infrastructure.projections.ProductMinProjection;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, FavoriteProductPK> {

    @Query("""
                select 
                    v.id as id,          
                    p.name as name,                   
                    p.basePrice as basePrice,
                    (p.basePrice + coalesce(v.priceAdjustment, 0) - coalesce(v.discountAmount, 0)) as finalPrice,
                    ((p.basePrice + coalesce(v.priceAdjustment, 0)) - coalesce(v.discountAmount, 0)) as discountBase,
                    (select i.imgUrl 
                     from ProductVariationImage i 
                     where i.variation.id = v.id 
                       and i.isPrimary = true 
                     order by i.id asc limit 1) as firstImageUrl
                from FavoriteProduct f
                join f.variation v
                join v.product p
                where f.user.id = :userId
            """)
    List<ProductMinProjection> findFavoriteProductsByUserId(@Param("userId") Long userId);


    boolean existsById(FavoriteProductPK id);

    void deleteById(FavoriteProductPK id);

}
