package com.bizmate.salesPages.management.order.order.repository;

import com.bizmate.salesPages.management.order.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {

    @Query("SELECT MAX(o.orderId) FROM Order o WHERE o.orderIdDate = :orderIdDate")
    Optional<String> findMaxOrderIdByOrderIdDate(@Param("orderIdDate") LocalDate today);

    @Query("SELECT o.orderId FROM Order o ORDER BY o.orderId ASC LIMIT 1")
    Optional<String> findMinOrderId();
}