package com.bizmate.salesPages.management.order.orderItem.service;

import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;

import java.util.List;

public interface OrderItemService {
    public List<OrderItem> orderItemList(OrderItem orderItem);
    public OrderItem orderItemInsert(OrderItem orderItem);
    public OrderItem orderItemUpdate(OrderItem orderItem);
    public void orderItemDelete(OrderItem orderItem);
}
