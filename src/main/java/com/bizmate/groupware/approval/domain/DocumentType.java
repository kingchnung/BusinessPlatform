package com.bizmate.groupware.approval.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    REQUEST("REQUEST", "품의서"),        // 품의서
    RESIGN("RESIGN", "퇴직서"),    // 퇴직서
    REPORT("REPORT", "보고서"),         // 보고서
    HR_MOVE("HR_MOVE", "인사발령");     // 인사발령/이동 신청서

    private final String code;
    private final String label;

    DocumentType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static  DocumentType from(String value) {
        if (value == null) return null;
        String v = value.trim().toUpperCase();

        for(DocumentType t : values()) {
            if(t.code.equalsIgnoreCase(v) || t.label.equalsIgnoreCase(v)) {
                return t;
            }
        }

        throw new IllegalArgumentException("유효하지 않은 문서유형: " + value);
    }

    @JsonValue // ✅ Enum → JSON 문자열 변환 (응답 시)
    public String toValue() {
        return this.code; // ← "REQUEST" 로 직렬화됨
    }

}