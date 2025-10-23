package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

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

    @Override
    public String convertToDatabaseColumn(List<ApproverStep> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) return "[]";
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("❌ ApproverLineJsonConverter DB 저장 실패: {}", e.getMessage(), e);
            return "[]";
        }
    }

    @Override
    public List<ApproverStep> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank() || dbData.equalsIgnoreCase("null")) {
                return Collections.emptyList();
            }
            return mapper.readValue(dbData, new TypeReference<List<ApproverStep>>() {});
        } catch (Exception e) {
            log.error("❌ ApproverLineJsonConverter 변환 실패: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
