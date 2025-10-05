package com.bizmate.groupware.approval.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ApprovalIdGenerator {

    // 부서코드와 날짜를 키로 사용하여 시퀀스 번호를 저장 (예: "HR-20250930-001" ...)
    private final ConcurrentHashMap<String, AtomicInteger> sequenceMap = new ConcurrentHashMap<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String SEQUENCE_FORMAT = "%03d";

    /**
     * 지정된 부서 코드를 기반으로 새로운 문서 ID를 생성합니다.
     * 형식: [DepartmentCode]-[YYYYMMDD]-[001]
     * * @param departmentCode 부서 코드 (예: "HR", "FIN")
     * @return 생성된 고유 문서 ID
     */

    public String generateNewId(String departmentCode) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String key = departmentCode.toUpperCase() + "-" + today;

        // 3. 시퀀스 번호 증가 (동시성 안전)
        // 해당 키가 없으면 0으로 초기화하고, get()으로 현재 값을 가져온 후 incrementAndGet()으로 1 증가 시킵니다.
        AtomicInteger currentSequence = sequenceMap.computeIfAbsent(key, k -> new AtomicInteger(0));
        int nextSequence = currentSequence.incrementAndGet();

        String sequencePart = String.format(SEQUENCE_FORMAT, nextSequence);
        return key + "-" + sequencePart;
    }

}
