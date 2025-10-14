package com.bizmate.salesPages.management.order.order.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.UserPrincipal;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
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
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String writerName = userPrincipal.getUsername();
        String writerId = userPrincipal.getUserId().toString();

        orderDTO.setUserId(writerId);
        orderDTO.setWriter(writerName);

        LocalDate today = LocalDate.now();

        // 1. 주문 일자 설정
        orderDTO.setOrderDate(today);

        // 2. 당일의 가장 큰 orderId 조회
        String maxOrderId = orderRepository.findMaxOrderIdByOrderDate(today).orElse(null);

        // 3. 다음 일련번호 계산
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

        // 4. 최종 orderId 생성
        String datePart = today.format(DATE_FORMAT);
        String sequencePart = String.format("%04d", nextSequence);
        String finalOrderId = datePart + "-" + sequencePart;

        orderDTO.setOrderId(finalOrderId);

        // 5. Order 객체 저장
        Order order = modelMapper.map(orderDTO, Order.class);
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

        order.changeOrderAmount(orderDTO.getOrderAmount());
        order.changeClientId(orderDTO.getClientId());
        order.changeClientCompany(orderDTO.getClientCompany());
        order.changeOrderDueDate(orderDTO.getOrderDueDate());
        order.changeOrderNote(orderDTO.getOrderNote());
        order.changeProjectId(orderDTO.getProjectId());
        order.changeProjectName(orderDTO.getProjectName());

        orderRepository.save(order);
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
