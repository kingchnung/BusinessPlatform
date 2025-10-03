package com.bizmate.groupware.service;

import com.bizmate.groupware.domain.ApprovalDocuments;
import com.bizmate.groupware.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.dto.ApproverDto;
import com.bizmate.groupware.repository.ApprovalDocumentsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalDocumentsServiceImpl implements ApprovalDocumentsService{

    private final ApprovalDocumentsRepository approvalDocumentsRepository;
    private final ApprovalIdGenerator idGenerator;
    private final ObjectMapper objectMapper;

    //새로운 결재 문서를 상신(등록)
    @Override
    @Transactional
    public ApprovalDocumentsDto createNewDocument(ApprovalDocumentsDto dto) throws JsonProcessingException {

        // [백엔드 유효성 검증]: 프론트엔드를 우회하는 요청으로부터 데이터 무결성을 보호
        if(dto.getDepartmentCode() == null || dto.getDepartmentCode().isEmpty()) {
            throw new IllegalArgumentException("문서 ID 생성을 위해 부서 코드가 필수입니다.");
        }

        if(dto.getApprovalLine() == null) {
            throw new IllegalArgumentException("결재선이 지정되지 않았습니다.");
        }

        //커스텀 ID 생성 (예 : HR-20251001-001)
        String customId = idGenerator.generateNewId(dto.getDepartmentCode());

        //DTO -> Entity 매핑 및 초기 상태 설정
        ApprovalDocuments documents = mapDtoToEntity(dto, customId);
        
//        documents.setStatus("승인대기");
//        documents.setCurrentApproverIndex(0);
        
        //JPA 저장 호출
        ApprovalDocuments savedDocument = approvalDocumentsRepository.save(documents);
        log.info("새 문서 상신 완료: DocId={}", savedDocument.getDocId());

        return mapEntityToDto(savedDocument);
    }

    //DTO -> Entity 변환 (저장 시 사용)
    private ApprovalDocuments mapDtoToEntity(ApprovalDocumentsDto dto, String customId) throws JsonProcessingException {

        ApprovalDocuments entity = new ApprovalDocuments();
        
        entity.setDocId(customId);
        entity.setTitle(dto.getTitle());
        entity.setDocType(dto.getDocType());

        entity.setStatus("승인대기");
        entity.setCurrentApproverIndex(0);

        entity.setAuthorUserId(dto.getUserId());
        entity.setAuthorRoleId(dto.getRoleId());
        entity.setAuthorEmpId(dto.getEmpId());

        entity.setApprovalLineJson(objectMapper.writeValueAsString(dto.getApprovalLine()));
        entity.setDocContentJson(objectMapper.writeValueAsString(dto.getDocContent()));

        return entity;
    }

    //Entity -> DTO 변환 (조회 및 반환 시 사용)
    private ApprovalDocumentsDto mapEntityToDto(ApprovalDocuments entity) {

        ApprovalDocumentsDto.ApprovalDocumentsDtoBuilder builder = ApprovalDocumentsDto.builder()
                .id(entity.getDocId())
                .title(entity.getTitle())
                .docType(entity.getDocType())
                .status(entity.getStatus())
                .finalDocNumber(entity.getFinalDocNumber())
                .userId(entity.getAuthorUserId())
                .roleId(entity.getAuthorRoleId())
                .empId(entity.getAuthorEmpId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        try {
            if(entity.getApprovalLineJson() != null) {
                builder.approvalLine(objectMapper.readValue(entity.getApprovalLineJson(), new TypeReference<List<ApproverDto>>() {}));
            }
            if(entity.getDocContentJson() != null) {
                builder.docContent(objectMapper.readValue(entity.getDocContentJson(), new TypeReference<Map<String, Object>>(){}));
            }

            //현재 결재자 ID는 결재선 JSON을 분석하여 설정해야 한다.
            // builder.currentApproverId(determineCurrentApprover(entity.getApprovalLineJson(), entity.getCurrentApproverIndex()));
        } catch (JsonProcessingException e) {
            log.error("JSON Parsing Error for Doc ID" + entity.getDocId() + ": " + e.getMessage());
        }

        return builder.build();
    }

}
