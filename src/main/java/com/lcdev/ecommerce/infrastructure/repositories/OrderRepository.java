package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.projections.OrderProjection;
import com.lcdev.ecommerce.infrastructure.projections.OrderSummaryWithImageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByClientAndStatusIn(User client, List<OrderStatus> status);

    List<Order> findByStatusAndExpirationMomentBefore(OrderStatus status, Instant now);

    @Query("""
                SELECT 
                    o.id AS id,
                    o.moment AS moment,
                    o.status AS status,
                    COALESCE(SUM(oi.price * oi.quantity), 0) AS subtotal,
                    o.shippingCost AS shippingCost,
                    o.discountApplied AS discountApplied,
                    CONCAT(c.firstName, ' ', c.lastName) AS clientName,
                    pay.moment AS paymentMoment
                FROM Order o
                LEFT JOIN o.client c
                LEFT JOIN o.payment pay
                LEFT JOIN o.items oi
                WHERE (:id IS NULL OR o.id = :id)
                  AND (:status IS NULL OR o.status = :status)
                  AND (:startDate IS NULL OR o.moment >= :startDate)
                  AND (:endDate IS NULL OR o.moment <= :endDate)
                  AND (:clientName IS NULL OR LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :clientName, '%')))
                GROUP BY o.id, o.moment, o.status, o.shippingCost, o.discountApplied, c.firstName, c.lastName, pay.moment
                ORDER BY o.status ASC, o.moment DESC
            """)
    Page<OrderProjection> search(
            @Param("id") Long id,
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("clientName") String clientName,
            Pageable pageable
    );


    @Query("""
                select o.id as id,
                       o.moment as moment,
                       o.status as status,
                       pay.paymentMethod as paymentMethod,
                       (o.shippingCost + sum(i.price * i.quantity) - coalesce(o.discountApplied, 0)) as total,
                       (
                          select pvi.imgUrl
                          from ProductVariationImage pvi
                          where pvi.variation.id = (
                              select min(oi2.id.variation.id)
                              from OrderItem oi2
                              where oi2.id.order.id = o.id
                          )
                          and pvi.isPrimary = true
                       ) as imgUrl
                from Order o
                join o.items i
                left join o.payment pay
                where o.client = :client
                group by o.id, o.moment, o.status, pay.paymentMethod, o.shippingCost, o.discountApplied
                order by o.moment desc
            """)
    List<OrderSummaryWithImageProjection> findOrdersSummaryWithImage(@Param("client") User client);


    @Query("""
                select o from Order o
                left join fetch o.client c
                left join fetch o.items i
                left join fetch i.id.variation v
                left join fetch v.product p
                left join fetch p.category cat
                left join fetch o.payment pay
                left join fetch o.coupon cp
                where o.client = :client and o.id = :id
            """)
    Optional<Order> findByIdAndClientWithDetails(@Param("id") Long id, @Param("client") User client);

    @Query("""
                select o from Order o
                left join fetch o.client c
                left join fetch o.items i
                left join fetch i.id.variation v
                left join fetch v.product p
                left join fetch p.category cat
                left join fetch o.payment pay
                left join fetch o.coupon cp
                where o.id = :id
            """)
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    @Query("""
                SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
                FROM Order o
                JOIN o.items i
                JOIN i.id.variation v
                WHERE o.client.id = :clientId
                  AND v.product.id = :productId
                  AND o.status = :status
            """)
    boolean existsByClientAndProductAndStatus(
            @Param("clientId") Long clientId,
            @Param("productId") Long productId,
            @Param("status") OrderStatus status
    );

}
