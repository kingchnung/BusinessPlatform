package com.bizmate.groupware.approval.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ApprovalIdGenerator {

    // 부서코드와 날짜를 키로 사용하여 시퀀스 번호를 저장 (예: "HR-20250930-001" ...)
    private final Map<String, AtomicInteger> sequenceMap = new ConcurrentHashMap<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String SEQUENCE_FORMAT = "%03d";

    /**
     * 부서 ID를 기반으로 새로운 문서 ID를 생성합니다.
     * 형식: [부서ID]-[YYYYMMDD]-[001]
     * 예시: 01-20251006-001
     */

    public String generateNewId(String departmentCode) {
        if(departmentCode == null || departmentCode.isBlank()) {
            throw new IllegalArgumentException("부서코드는 비어 있을 수 없습니다.");
        }

        String today = LocalDate.now().format(DATE_FORMATTER);
        String key = departmentCode + "-" + today;

        // 동시성 안전 시퀀스 증가
        AtomicInteger currentSeq = sequenceMap.computeIfAbsent(key, k -> new AtomicInteger(0));
        int nextSeq = currentSeq.incrementAndGet();

        return key + "-" + String.format(SEQUENCE_FORMAT, nextSeq);
    }

}
