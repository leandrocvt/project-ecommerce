package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.ReturnItem;
import com.lcdev.ecommerce.domain.entities.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, Long> {
}
