package com.bizmate.groupware.approval.domain;

public enum DocumentStatus {
    DRAFT,          // 임시저장(기안중)
    IN_PROGRESS,    // 결재 진행 중
    REJECTED,       // 반려
    APPROVED,       // 최종 승인 완료
    ARCHIVED,       // 보존/폐기로 사용자 목록에서 숨김
    DELETED       // 명확히 폐기 상태로 별도 구분하고 싶을 때
}
