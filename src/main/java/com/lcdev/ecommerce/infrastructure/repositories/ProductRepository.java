package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.infrastructure.projections.ProductMinProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
        WITH filtered_variations AS (
            SELECT
                pv.*,
                ROW_NUMBER() OVER (
                    PARTITION BY pv.product_id
                    ORDER BY pv.id
                ) AS rn
            FROM tb_product_variation pv
            JOIN tb_product p ON p.id = pv.product_id
            WHERE (:size IS NULL OR pv.size = :size)
              AND (:minPrice IS NULL OR (p.base_price + COALESCE(pv.price_adjustment,0) - COALESCE(pv.discount_amount,0)) >= :minPrice)
              AND (:maxPrice IS NULL OR (p.base_price + COALESCE(pv.price_adjustment,0) - COALESCE(pv.discount_amount,0)) <= :maxPrice)
        ),
        first_images AS (
            SELECT
                img.variation_id,
                img.img_url,
                ROW_NUMBER() OVER (
                    PARTITION BY img.variation_id
                    ORDER BY img.is_primary DESC
                ) AS rn
            FROM tb_product_variation_images img
        ),
        category_tree AS (
            SELECT c.id
            FROM tb_category c
            WHERE (:categoryId IS NULL OR c.id = :categoryId OR c.parent_id = :categoryId)
        )
        SELECT *
        FROM (
            SELECT
                p.id,
                p.name,
                p.base_price AS basePrice,
                (p.base_price + COALESCE(fv.price_adjustment,0) - COALESCE(fv.discount_amount,0)) AS finalPrice,
                CASE
                    WHEN (p.base_price + COALESCE(fv.price_adjustment,0)) > 0
                        THEN (COALESCE(fv.discount_amount,0) * 100 / (p.base_price + COALESCE(fv.price_adjustment,0)))
                    ELSE 0
                END AS discountPercent,
                fi.img_url AS firstImageUrl
            FROM tb_product p
            INNER JOIN filtered_variations fv
                ON fv.product_id = p.id AND fv.rn = 1
            INNER JOIN first_images fi
                ON fi.variation_id = fv.id AND fi.rn = 1
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:categoryId IS NULL OR p.category_id IN (SELECT id FROM category_tree))
        ) sub
        ORDER BY
          CASE WHEN :sort = 'highestPrice' THEN sub.finalPrice END DESC,
          CASE WHEN :sort = 'lowestPrice' THEN sub.finalPrice END ASC,
          CASE WHEN :sort = 'biggestDiscount' THEN sub.discountPercent END DESC,
          CASE WHEN :sort = 'relevance' THEN sub.id END DESC,
          sub.name ASC
        """,
            countQuery = """
                SELECT COUNT(*)
                FROM tb_product p
                WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
                  AND (:categoryId IS NULL OR p.category_id IN (
                        SELECT c.id
                        FROM tb_category c
                        WHERE c.id = :categoryId OR c.parent_id = :categoryId
                    ))
                """,
            nativeQuery = true
    )
    Page<ProductMinProjection> search(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("size") String size,
            @Param("sort") String sort,
            Pageable pageable
    );

}
