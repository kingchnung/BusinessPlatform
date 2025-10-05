package com.bizmate.groupware.approval.dto;

import com.bizmate.groupware.approval.domain.ApproverStep;
import com.bizmate.groupware.approval.domain.DocumentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDocumentsDto {

    // 1. 문서 식별 및 기본정보(조회 시 사용, 등록 시에는 서버에서 생성됨)
    private String id;

    @NotEmpty(message = "문서 제목은 필수입니다.")
    private String title;

    @NotNull(message = "문서 타입은 필수입니다.")
    private DocumentType docType;
    
    private String status;          //반환용 문자열(예 : PENDING)
    private String departmentCode;  //커스텀 ID 생성에 사용
    private String finalDocNumber;

    // 2. 기안자 정보 (DB FK와 매핑되는 필드)
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;    //기안자 사용자 ID
    @NotNull(message = "역할 ID는 필수입니다.")
    private Long roleId;    //기안자의 역할 ID
    @NotNull(message = "사번은 필수입니다.")
    private Long empId;     //기안자의 사번

    // 3. 문서 내용 및 결재선 (JSON 문자열을 Java Object로 파싱하여 전달)
    @Valid
    @NotNull(message = "문서 본문은 필수입니다.")
    @NotEmpty(message = "문서 본문은 비어 있을 수 없습니다.")
    private Map<String, Object> docContent;      //문서 본문 내용(JSON 객체) JSON object

    @Valid
    @NotNull(message = "결재선은 필수입니다.")
    @NotEmpty(message = "결재선은 1명 이상이어야 합니다.")
    private List<ApproverStep> approvalLine;    //결재선 정보(JSON 배열) JSON array

    //4. 시간 정보(BaseEntity 필드와 유사)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 5. 현재 결재 정보 (문서 흐름 관리에 필요)
    private String currentApproverId;
}
