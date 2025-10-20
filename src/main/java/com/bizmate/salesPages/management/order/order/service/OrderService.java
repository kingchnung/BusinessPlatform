package com.bizmate.salesPages.management.order.order.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    String register(OrderDTO orderDTO);
    OrderDTO get(String orderId);
    void modify(OrderDTO orderDTO);
    void remove(String orderId);
    PageResponseDTO<OrderDTO> list(PageRequestDTO pageRequestDTO);
}
