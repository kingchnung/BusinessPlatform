package com.bizmate.salesPages.management.order.order.service;

import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.order.orderItem.dto.OrderItemDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final ModelMapper modelMapper;

    @Override
    public String register(OrderDTO orderDTO) {
       LocalDate today = LocalDate.now();
        orderDTO.setOrderDate(today);

        String maxOrderId = orderRepository.findMaxOrderIdByOrderDate(today).orElse(null);

        int nextSequence = 1;
        if(maxOrderId != null) {
            try {
                // 기존 ID에서 일련번호 4자리 추출
                String seqStr = maxOrderId.substring(9);
                nextSequence = Integer.parseInt(seqStr) + 1;
            } catch (Exception e){
                // 파싱 오류 발생 시 1로 시작(비정상 케이스)
                nextSequence = 1;
            }
        }

        String datePart = today.format(DATE_FORMAT);
        String sequencePart = String.format("%04d", nextSequence);
        String finalOrderId = datePart + "-" + sequencePart;
        orderDTO.setOrderId(finalOrderId);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDTO userDTO){
            orderDTO.setUserId(userDTO.getUsername());
            orderDTO.setWriter(userDTO.getEmpName());
        } else {
            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
        }

        Order order = modelMapper.map(orderDTO, Order.class);

        List<OrderItem> newOrderItem = orderDTO.getOrderItems().stream()
                        .map(itemDTO -> modelMapper.map(itemDTO, OrderItem.class))
                                .collect(Collectors.toList());
        order.updateOrderItems(newOrderItem);

        order.calculateOrderAmount();

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getOrderId();
    }

    @Override
    public OrderDTO get(String orderId) {
        Optional<Order> result = orderRepository.findById(orderId);
        Order order = result.orElseThrow();
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        return dto;
    }

    @Override
    public void modify(OrderDTO orderDTO) {
        Optional<Order> result = orderRepository.findById(orderDTO.getOrderId());
        Order order = result.orElseThrow();

        order.changeClientId(orderDTO.getClientId());
        order.changeOrderDueDate(orderDTO.getOrderDueDate());
        order.changeOrderNote(orderDTO.getOrderNote());
        order.changeProjectId(orderDTO.getProjectId());

        List<OrderItemDTO> newItemDto = orderDTO.getOrderItems();
        List<OrderItem> mergedItem = new ArrayList<>();

        for(OrderItemDTO itemDTO : newItemDto){
            if(itemDTO.getOrderItemId() != null){
                OrderItem existingItem = order.getOrderItems().stream()
                        .filter(item -> itemDTO.getOrderItemId().equals(item.getOrderItemId()))
                        .findFirst()
                        .orElse(null);

                if(existingItem != null){
                    existingItem.changeItemName(itemDTO.getItemName());
                    existingItem.changeQuantity(itemDTO.getQuantity());
                    existingItem.changeUnitPrice(itemDTO.getUnitPrice());
                    existingItem.changeUnitVat(itemDTO.getUintVat());
                    existingItem.changeItemNote(itemDTO.getItemNote());

                    existingItem.calculateAmount();
                    mergedItem.add(existingItem);
                }
            } else {
                OrderItem newItem = modelMapper.map(itemDTO, OrderItem.class);

                newItem.calculateAmount();
                mergedItem.add(newItem);
            }
        }

        order.updateOrderItems(mergedItem);
        order.calculateOrderAmount();
    }

    @Override
    public void remove(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public PageResponseDTO<OrderDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("orderId").descending());

        Page<Order> result = orderRepository.findAll(pageable);
        List<OrderDTO> dtoList = result.getContent().stream().map(
                order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
        long totalCount = result.getTotalElements();

        PageResponseDTO<OrderDTO> responseDTO = PageResponseDTO.<OrderDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

        return responseDTO;
    }
}
