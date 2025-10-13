package com.bizmate.salesPages.management.order.order.controller;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;
import com.bizmate.salesPages.management.order.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public OrderDTO get(@PathVariable(name = "orderId") String orderId){
        return orderService.get(orderId);
    }

    @GetMapping("/orderList")
    public PageResponseDTO<OrderDTO> List(PageRequestDTO pageRequestDTO){
        return orderService.list(pageRequestDTO);
    }

    @PostMapping(value = "/")
    public Map<String, String> register(@RequestBody OrderDTO orderDTO){
        String orderId = orderService.register(orderDTO);
        return Map.of("OrderId", orderId);
    }

    @PutMapping("/{orderId}")
    public Map<String, String> modify(@PathVariable(name = "orderId")String orderId, @RequestBody OrderDTO orderDTO){
        orderDTO.setOrderId(orderId);
        orderService.modify(orderDTO);
        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/{orderId}")
    public Map<String, String> remove(@PathVariable(name = "orderId") String orderId){
        orderService.remove(orderId);
        return Map.of("RESULT","SUCCESS");
    }

}
