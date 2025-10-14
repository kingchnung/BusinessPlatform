package com.bizmate.salesPage.management.order.orderItem.repository;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@Slf4j
public class OrderItemRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void orderItemInsertTest(){
        Order order = Order.builder()
                .orderId("20251007-55")
                .projectId("210415")
                .projectName("테스트용")
                .orderDueDate(LocalDate.now())
                .orderAmount(new BigDecimal("2500000"))
                .userId("user00")
                .orderNote("테스트")
                .writer("한유주")
                .clientId("132-86-156477")
                .clientCompany("테스트기업")
                .build();

        OrderItem orderItem = OrderItem.builder()
                .itemName("orderItem 테스트 품목1")
                .quantity(2L)
                .unitPrice(new BigDecimal("2504000"))
                .vat(new BigDecimal("25040"))
                .totalAmount(new BigDecimal("2754400"))
                .build();
        order.addOrderItem(orderItem);

        log.info(" ### orderItem 생성 및 Order에 추가");

        OrderItem orderItem1 = OrderItem.builder()
                .itemName("orderItem 테스트 품목2")
                .quantity(1L)
                .unitPrice(new BigDecimal("2504000"))
                .vat(new BigDecimal("25040"))
                .totalAmount(new BigDecimal("2754400"))
                .build();
        order.addOrderItem(orderItem1);

        log.info(" ### orderItem 생성 및 Order에 추가2");

        OrderItem orderItem2 = OrderItem.builder()
                .itemName("orderItem 테스트 품목3")
                .quantity(11L)
                .unitPrice(new BigDecimal("2504000"))
                .vat(new BigDecimal("25040"))
                .totalAmount(new BigDecimal("2754400"))
                .build();
        order.addOrderItem(orderItem2);

        log.info(" ### orderItem 생성 및 Order에 추가3");

        orderRepository.saveAndFlush(order);
        log.info(" ### Order 엔티티와 모든 OrderItem 저장 완료");

        Order savedOrder = orderRepository.findById("20251007-55").orElseThrow();
        log.info("저장된 OrderItem 개수 : {} ", savedOrder.getOrderItems().size());
    }
}
