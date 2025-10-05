package com.bizmate.groupware.approval.domain;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.groupware.approval.infrastructure.ApproverLineJsonConverter;
import com.bizmate.groupware.approval.infrastructure.JsonMapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "approval_documents")
public class ApprovalDocuments extends BaseEntity {

    @Id
    @Column(name = "doc_id", length = 40, nullable = false)
    private String docId;   //부서코드 + 시퀀스

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 20)
    private DocumentType docType;

    @Column(name = "doc_title", nullable = false, length = 200)
    private String title;


    @Enumerated(EnumType.STRING)
    @Column(name = "doc_status", nullable = false, length = 20)
    private DocumentStatus status;

    @Column(name = "final_doc_number", unique = true)
    @Comment("최종 승인 시 발급되는 문서번호(연속 시퀀스)")
    private String finalDocNumber;  //최종 채번

    //FK
    @Column(name = "user_id", nullable = false, length = 40)
    private Long authorUserId; //기안자 ID
    @Column(name = "role_id")
    private Long authorRoleId;
    @Column(name = "emp_id")
    private Long authorEmpId;

    // 결재 흐름 관리 필드(현재 순서)
    @Column(name = "current_approver_index")
    private int currentApproverIndex;

    @Lob
    @Convert(converter = ApproverLineJsonConverter.class)
    @Column(name = "approval_line")
    private List<ApproverStep> approvalLine;

    @Lob
    @Convert(converter = JsonMapConverter.class)
    @Column(name = "doc_data")
    private Map<String, Object> docContent;

    @ElementCollection
    @CollectionTable(name = "approval_doc_viewers", joinColumns = @JoinColumn(name = "doc_id"))
    @Column(name = "viewer_id", length = 40)
    private List<String> viewerIds;

//    public int currentOrder() {
//        return approverLineJson.stream().filter(s -> s.decision() == Decision.PENDING)
//                .mapToInt(ApproverStep::order).min().orElse(Integer.MAX_VALUE);
//    }
//
//    public String currentApproverId() {
//        return approverLineJson.stream().filter(s -> s.decision() == Decision.PENDING)
//                .sorted(Comparator.comparingInt(ApproverStep::order))
//                .map(ApproverStep::approverId).findFirst().orElse(null);
//    }

    @Version
    @Column(name = "version")
    private Long version; //동시 결재/수정 충돌 방지.

    public boolean isDeletable() {
        return status == DocumentStatus.DRAFT || status == DocumentStatus.REJECTED;
    }
}
