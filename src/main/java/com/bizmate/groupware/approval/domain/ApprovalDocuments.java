package com.bizmate.groupware.approval.domain;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.groupware.approval.infrastructure.ApproverLineJsonConverter;
import com.bizmate.groupware.approval.infrastructure.JsonMapConverter;
import com.bizmate.groupware.approval.infrastructure.DocumentTypeConverter;

// ✅ HR 모듈의 엔티티 import
import com.bizmate.hr.domain.Departments;
import com.bizmate.hr.domain.Employees;
import com.bizmate.hr.domain.Roles;
import com.bizmate.hr.domain.Users;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "APPROVAL_DOCUMENTS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDocuments extends BaseEntity {

    @Id
    @Column(name = "DOC_ID", length = 40, nullable = false)
    private String docId;

    // ✅ Enum ↔ String 변환 컨버터 사용
    @Convert(converter = DocumentTypeConverter.class)
    @Column(name = "DOC_TYPE", nullable = false, length = 255)
    private DocumentType docType;

    @Column(name = "DOC_TITLE", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOC_STATUS", nullable = false, length = 20)
    private DocumentStatus status;

    // ✅ HR 모듈 Departments 참조
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPARTMENT_ID", nullable = false)
    private Departments department;

    @Column(name = "FINAL_DOC_NUMBER", unique = true)
    @Comment("최종 승인 시 발급되는 문서번호(연속 시퀀스)")
    private String finalDocNumber;

    // ✅ HR Users 참조 (기안자)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users authorUser;

    // ✅ HR Roles 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    private Roles authorRole;

    // ✅ HR Employees 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private Employees authorEmployee;

    @Column(name = "CURRENT_APPROVER_INDEX")
    @ColumnDefault("0")
    private int currentApproverIndex;

    @Lob
    @Column(name = "APPROVAL_LINE")
    @Convert(converter = ApproverLineJsonConverter.class)
    private List<ApproverStep> approvalLine;

    @Lob
    @Column(name = "DOC_DATA")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> docContent;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "APPROVAL_DOC_VIEWERS", joinColumns = @JoinColumn(name = "DOC_ID"))
    @Column(name = "VIEWER_ID", length = 40)
    private List<String> viewerIds = new ArrayList<>();

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Builder.Default
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileAttachment> attachments = new ArrayList<>();

    public boolean isDeletable() {
        return status == DocumentStatus.DRAFT || status == DocumentStatus.REJECTED;
    }
}