package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.projections.OrderSummaryWithImageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByClientAndStatusIn(User client, List<OrderStatus> statuses);

    List<Order> findByStatusAndExpirationMomentBefore(OrderStatus status, Instant now);

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
                left join fetch o.items i
                left join fetch i.id.variation v
                left join fetch v.product p
                left join fetch p.category c
                left join fetch o.payment pay
                left join fetch o.coupon cp
                where o.client = :client and o.id = :id
            """)
    Optional<Order> findByIdAndClientWithDetails(@Param("id") Long id, @Param("client") User client);

}
