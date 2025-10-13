package com.bizmate.salesPage.management.order.order.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;
import com.bizmate.salesPages.management.order.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
@Slf4j
public class OrderServiceTests {
    @Autowired
    private OrderService orderService;

    @Test
    public void testRegister() {
        OrderDTO orderDTO = OrderDTO.builder()
                .projectId("210405-23")
                .projectName("2번째 주문 테스트")
                .orderAmount(new BigDecimal("10000000.00"))
                .userId("user003")
                .writer("유주짱")
                .clientId("444-55-66666")
                .clientCompany("테스트 컴퍼니 A")
                .orderNote("테스트 주문 2")
                .build();

        String orderId = orderService.register(orderDTO);
        log.info("생성된 주문번호 : {}", orderId);
    }

    @Test
    public void testGet() {
        String orderId = "20251007-1";
        OrderDTO orderDTO = orderService.get(orderId);
        log.info("데이터: {}", orderDTO);
    }

    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(2).size(10).build();

        PageResponseDTO<OrderDTO> response = orderService.list(pageRequestDTO);
        log.info("PageResponseDTO: {}", response);
    }
}
