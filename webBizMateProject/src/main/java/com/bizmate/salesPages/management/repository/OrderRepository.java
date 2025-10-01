package com.bizmate.salesPages.management.repository;

import com.bizmate.salesPages.management.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT MAX(o.orderId) FROM Order o WHERE o.orderDate = :orderDate")
    Optional<String> findMaxOrderIdByOrderDate(@Param("orderDate") LocalDate today);
}