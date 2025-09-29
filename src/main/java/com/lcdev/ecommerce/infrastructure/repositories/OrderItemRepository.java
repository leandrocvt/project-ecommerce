package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.OrderItem;
import com.lcdev.ecommerce.domain.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

    @Query("""
                SELECT oi FROM OrderItem oi
                JOIN FETCH oi.id.variation v
                JOIN FETCH v.product p
                WHERE oi.id.order.id = :orderId
                  AND v.id IN :variationIds
            """)
    List<OrderItem> findAllByOrderIdAndVariationIds(
            @Param("orderId") Long orderId,
            @Param("variationIds") List<Long> variationIds
    );

}
