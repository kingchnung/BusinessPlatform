package com.bizmate.salesPage.management.order.order.repository;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Slf4j
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testInsert() {
        for(int i = 1; i<= 10; i++) {
            Order order = Order.builder()
                    .orderId("20251007-" + i)
                    .projectId("210415-" + i)
                    .projectName("테스트용" + i)
                    .orderDueDate(LocalDate.now())
                    .orderAmount(new BigDecimal(i + "2500000"))
                    .userId("user00" + i)
                    .orderNote("테스트!")
                    .writer("한유주")
                    .clientId("132-86-156477")
                    .clientCompany("테스트기업" + i)
                    .build();

            orderRepository.save(order);
        }
    }

    @Test
    public void testRead() {
        String orderId = "20251007-1";

        Optional<Order> result = orderRepository.findById(orderId);
        Order order = result.orElseThrow();
        log.info("데이터조회(20251007-1): {}", order);
    }

    @Test
    public void testModify(){
        String orderId = "20251007-1";

        Optional<Order> result = orderRepository.findById(orderId);

        Order order = result.orElseThrow();
        order.changeClientCompany("유주컴퍼니");
        order.changeClientId("111-22-111111");
        order.changeOrderAmount(new BigDecimal("132000000"));

        Order orderResult = orderRepository.save(order);
        log.info("수정된 데이터: {}", orderResult);
    }

    @Test
    public void testDelete() {
        String orderId = "20251007-3";

        orderRepository.deleteById(orderId);
    }

    @Test
    public void testPaging() {
        Pageable pageable = PageRequest.of(0,10, Sort.by("orderId").descending());
        Page<Order> result = orderRepository.findAll(pageable);
        log.info("총 데이터 수: {}", result.getTotalElements());
        result.getContent().stream().forEach(order -> log.info(order.toString()));
    }


}
