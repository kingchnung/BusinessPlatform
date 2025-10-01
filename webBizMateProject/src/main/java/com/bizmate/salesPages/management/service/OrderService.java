package com.bizmate.salesPages.management.service;

import com.bizmate.salesPages.management.domain.Order;
import com.bizmate.salesPages.management.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class OrderService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    @Autowired
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrderWithCustomId(Order newOrder){
        LocalDate today = LocalDate.now();

        // 1. 주문 일자 설정
        newOrder.setOrderDate(today);

        // 2. 당일의 가장 큰 orderId 조회
        String maxOrderId = orderRepository.findMaxOrderIdByOrderDate(today).orElse(null);

        // 3. 다음 일련번호 계산
        int nextSequence = 1;
        if(maxOrderId != null) {
            try {
                // 기존 ID에서 일련번호 4자리 추출
                String seqStr = maxOrderId.substring(8);
                nextSequence = Integer.parseInt(seqStr) + 1;
            } catch (Exception e){
                // 파싱 오류 발생 시 1로 시작(비정상 케이스)
                nextSequence = 1;
            }
        }

        // 4. 최종 orderId 생성
        String datePart = today.format(DATE_FORMAT);
        String sequencePart = String.format("%04d", nextSequence);
        String finalOrderId = datePart + sequencePart;

        newOrder.setOrderId(finalOrderId);

        // 5. Order 객체 저장
        return orderRepository.save(newOrder);
    }
}
