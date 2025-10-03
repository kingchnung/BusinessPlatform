package com.bizmate.groupware.domain;

import com.bizmate.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "approval_documents")
public class ApprovalDocuments extends BaseEntity {

    @Id
    @Column(name = "doc_id")
    private String docId;

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "doc_title")
    private String title;

    @Column(name = "doc_status")
    private String status;

    @Column(name = "final_doc_number", unique = true, nullable = true)
    private String finalDocNumber;

    //FK
    @Column(name = "user_id")
    private Long authorUserId;

    //FK
    @Column(name = "role_id")
    private Long authorRoleId;

    //FK
    @Column(name = "emp_id")
    private Long authorEmpId;

    // 결재 흐름 관리 필드(현재 순서)
    @Column(name = "current_approver_index")
    private int currentApproverIndex;

    @Lob
    @Column(name = "approval_line")
    private String approvalLineJson;

    @Lob
    @Column(name = "doc_data")
    private String docContentJson;
}
