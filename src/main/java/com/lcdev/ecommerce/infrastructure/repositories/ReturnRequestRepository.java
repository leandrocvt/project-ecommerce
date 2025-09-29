package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.ReturnRequest;
import com.lcdev.ecommerce.domain.enums.ReturnStatus;
import com.lcdev.ecommerce.infrastructure.projections.ReturnProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    @Query("""
            SELECT DISTINCT r FROM ReturnRequest r
            JOIN FETCH r.order o
            JOIN FETCH r.user u
            LEFT JOIN FETCH r.items ri
            LEFT JOIN FETCH ri.orderItem oi
            LEFT JOIN FETCH oi.id.variation v
            LEFT JOIN FETCH v.product p
            WHERE o.id = :orderId AND u.id = :userId
            """)
    List<ReturnRequest> findByOrderIdAndUserIdWithDetails(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    @Query("""
                SELECT DISTINCT r FROM ReturnRequest r
                JOIN FETCH r.order o
                JOIN FETCH r.user u
                LEFT JOIN FETCH r.items ri
                LEFT JOIN FETCH ri.orderItem oi
                LEFT JOIN FETCH oi.id.variation v
                LEFT JOIN FETCH v.product p
                WHERE r.id = :id AND u.id = :userId
            """)
    Optional<ReturnRequest> findByIdAndUserIdWithDetails(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    boolean existsByOrderIdAndStatusIn(Long orderId, Collection<ReturnStatus> statuses);

    @Query("""
                SELECT COALESCE(SUM(ri.quantity), 0)
                FROM ReturnItem ri
                JOIN ri.returnRequest rr
                WHERE rr.order.id = :orderId
                  AND ri.orderItem.id.variation.id = :variationId
                  AND rr.status IN (:validStatuses)
            """)
    int sumReturnedQuantity(
            @Param("orderId") Long orderId,
            @Param("variationId") Long variationId,
            @Param("validStatuses") Collection<ReturnStatus> validStatuses
    );

    @Query("""
            SELECT
                r.id AS id,
                r.requestedAt AS createdAt,
                r.status AS status,
                CONCAT(u.firstName, ' ', u.lastName) AS clientName,
                o.id AS orderId,
                COALESCE(SUM(ri.refundAmount), 0) AS totalRefund
            FROM ReturnRequest r
            JOIN r.user u
            JOIN r.order o
            LEFT JOIN r.items ri
            WHERE (:id IS NULL OR r.id = :id)
              AND (:status IS NULL OR r.status = :status)
              AND (:startDate IS NULL OR r.requestedAt >= :startDate)
              AND (:endDate IS NULL OR r.requestedAt <= :endDate)
              AND (:clientName IS NULL OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :clientName, '%')))
            GROUP BY r.id, r.requestedAt, r.status, u.firstName, u.lastName, o.id
            ORDER BY r.status ASC, r.requestedAt DESC
            """)
    Page<ReturnProjection> search(
            @Param("id") Long id,
            @Param("status") ReturnStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("clientName") String clientName,
            Pageable pageable
    );

}

