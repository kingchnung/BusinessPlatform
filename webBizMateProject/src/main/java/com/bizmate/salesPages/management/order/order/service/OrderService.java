package com.bizmate.salesPages.management.order.order.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;
<<<<<<< HEAD

public interface OrderService {
    public String register(OrderDTO orderDTO);
    public OrderDTO get(String orderId);
    public void modify(OrderDTO orderDTO);
    public void remove(String orderId);
    public PageResponseDTO<OrderDTO> list(PageRequestDTO pageRequestDTO);
=======
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    String register(OrderDTO orderDTO);
    OrderDTO get(String orderId);
    void modify(OrderDTO orderDTO);
    void remove(String orderId);
    PageResponseDTO<OrderDTO> list(PageRequestDTO pageRequestDTO);
>>>>>>> 7e631613e802f528445a8f222c1ec078e9c8bda3
}
