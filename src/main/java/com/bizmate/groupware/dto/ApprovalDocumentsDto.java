package com.bizmate.groupware.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovalDocumentsDto {

    // 1. 문서 식별 및 기본정보(조회 시 사용, 등록 시에는 서버에서 생성됨)
    private String id;
    private String title;
    private String docType;
    private String status;
    private String departmentCode;
    private String finalDocNumber;

    // 2. 기안자 정보 (DB FK와 매핑되는 필드)
    private Long userId;    //기안자 사용자 ID
    private Long roleId;    //기안자의 역할 ID
    private Long empId;     //기안자의 사번

    // 3. 문서 내용 및 결재선 (JSON 문자열을 Java Object로 파싱하여 전달)
    private Object docContent;      //문서 본문 내용(JSON 객체)
    private Object approvalLine;    //결재선 정보(JSON 배열)

    //4. 시간 정보(BaseEntity 필드와 유사)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 5. 현재 결재 정보 (문서 흐름 관리에 필요)
    private String currentApproverId;
}
