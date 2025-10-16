package com.bizmate.salesPages.management.order.order.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;

public interface OrderService {
    public String register(OrderDTO orderDTO);
    public OrderDTO get(String orderId);
    public void modify(OrderDTO orderDTO);
    public void remove(String orderId);
    public PageResponseDTO<OrderDTO> list(PageRequestDTO pageRequestDTO);
}