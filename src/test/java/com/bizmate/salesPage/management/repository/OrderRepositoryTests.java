//package com.bizmate.salesPage.management.repository;
//
//import com.bizmate.salesPages.management.domain.Order;
//import com.bizmate.salesPages.management.repository.OrderRepository;
//import com.bizmate.salesPages.management.service.OrderService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@SpringBootTest
//@Slf4j
//public class OrderRepositoryTests {
//    @Autowired
//    private OrderService orderService;
//    @Autowired
//    private OrderRepository orderRepository;

//    @Test
//    public void createOrderWithCustomIdTests() {
//        Order newOrder = Order.builder()
//                .projectId(20411L)
//                .projectName("웹사이트 구축 프로젝트 A")
//                .orderDueDate(LocalDate.now().plusDays(30))
//                .orderAmount(new BigDecimal("150000000.00"))
//                .employeeId(6480L)
//                .writer("한유주")
//                .clientId("132-86-11701")
//                .clientCompany("유주컴퍼니")
//                .orderNote("서비스에서 ID를 생성하는 테스트")
//                .build();
//
//        orderRepository.save(newOrder);
//        log.info("생성된 주문번호 : {}", newOrder.getOrderId());
//    }

//    @Test
//    public void createOrderWithCustomIdTests() {
//        LocalDate today = LocalDate.now();
//
//        Order newOrder = Order.builder()
//                .projectId(2001L)
//                .projectName("첫 번째 주문 테스트")
//                .orderDueDate(today.plusDays(10))
//                .orderAmount(new BigDecimal("5000000.00"))
//                .employeeId(3001L)
//                .writer("테스터1")
//                .clientId("444-55-66666")
//                .clientCompany("테스트 컴퍼니 A")
//                .orderNote("테스트 주문 1")
//                .build();
//
//        Order savedOrder = orderService.createOrderWithCustomId(newOrder);
//        log.info("생성된 주문번호 : {}", newOrder.getOrderId());
//
//        Order newOrder1 = Order.builder()
//                .projectId(2002L)
//                .projectName("2번째 주문 테스트")
//                .orderDueDate(today.plusDays(20))
//                .orderAmount(new BigDecimal("10000000.00"))
//                .employeeId(3004L)
//                .writer("테스터2")
//                .clientId("444-55-66666")
//                .clientCompany("테스트 컴퍼니 A")
//                .orderNote("테스트 주문 2")
//                .build();
//
//        Order savedOrder1 = orderService.createOrderWithCustomId(newOrder1);
//        log.info("생성된 주문번호 : {}", newOrder1.getOrderId());
//    }

//    @Test
//    public void deleteTests() {
//        Order deleteOrder = orderRepository.deleteById();
//    }
//}
