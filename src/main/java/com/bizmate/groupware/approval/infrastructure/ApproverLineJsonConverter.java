package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class ApproverLineJsonConverter implements AttributeConverter<List<ApproverStep>, String> {
    private static final ObjectMapper om = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(List<ApproverStep> attribute) {
        try {
            if(attribute == null || attribute.isEmpty()) return "[]";
            return om.writeValueAsString(attribute);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Serialize approverLine failed", e);
        }
    }

    @Override
    public List<ApproverStep> convertToEntityAttribute(String dbData) {
       try {
           if(dbData == null || dbData.isBlank()) {
               return Collections.emptyList();
           }
           return om.readValue(dbData, new TypeReference<List<ApproverStep>>() {});

       } catch (Exception e) {
           e.printStackTrace();
           throw new IllegalStateException("Deserialize approverLine failed", e);
       }
    }
}
