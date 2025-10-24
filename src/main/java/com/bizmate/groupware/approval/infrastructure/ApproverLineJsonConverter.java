package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ✅ ApproverLineJsonConverter
 * - List<ApproverStep> ↔ JSON 문자열 변환
 * - null, 빈 JSON([]) 대응
 */
@Slf4j
@Converter(autoApply = true)
public class ApproverLineJsonConverter implements AttributeConverter<List<ApproverStep>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ApproverStep> attribute) {
        try {
            if (attribute == null) return "[]";
            if (attribute.isEmpty()) return "[]";
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("❌ ApproverLine 직렬화 오류: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<ApproverStep> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) return new ArrayList<>();
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("❌ ApproverLine 역직렬화 오류: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
