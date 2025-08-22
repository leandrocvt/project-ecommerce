package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Assessment;
import com.lcdev.ecommerce.infrastructure.projections.AssessmentProjection;
import com.lcdev.ecommerce.infrastructure.projections.ReviewSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    @Query("""
    SELECT 
        a.id AS id,
        a.score AS score,
        a.comment AS comment,
        a.photoUrl AS photoUrl,
        CONCAT(u.firstName, ' ', u.lastName) AS username,
        a.createdAt AS createdAt
    FROM Assessment a
    JOIN a.user u
    WHERE a.product.id = :productId
    """)
    Page<AssessmentProjection> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("""
        SELECT AVG(a.score) as averageScore, COUNT(a) as totalReviews
        FROM Assessment a
        WHERE a.product.id = :productId
    """)
    ReviewSummaryProjection findReviewSummaryByProductId(@Param("productId") Long productId);

}

