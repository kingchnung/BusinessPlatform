package com.bizmate.groupware.approval.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApprovalDocuments is a Querydsl query type for ApprovalDocuments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalDocuments extends EntityPathBase<ApprovalDocuments> {

    private static final long serialVersionUID = 1359107073L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApprovalDocuments approvalDocuments = new QApprovalDocuments("approvalDocuments");

    public final com.bizmate.common.domain.QBaseEntity _super = new com.bizmate.common.domain.QBaseEntity(this);

    public final ListPath<ApproverStep, SimplePath<ApproverStep>> approvalLine = this.<ApproverStep, SimplePath<ApproverStep>>createList("approvalLine", ApproverStep.class, SimplePath.class, PathInits.DIRECT2);

    public final StringPath approvedBy = createString("approvedBy");

    public final DateTimePath<java.time.LocalDateTime> approvedDate = createDateTime("approvedDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> approvedEmpId = createNumber("approvedEmpId", Long.class);

    public final ListPath<FileAttachment, QFileAttachment> attachments = this.<FileAttachment, QFileAttachment>createList("attachments", FileAttachment.class, QFileAttachment.class, PathInits.DIRECT2);

    public final com.bizmate.hr.domain.QEmployee authorEmployee;

    public final com.bizmate.hr.domain.QRole authorRole;

    public final com.bizmate.hr.domain.QUserEntity authorUser;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Integer> currentApproverIndex = createNumber("currentApproverIndex", Integer.class);

    public final StringPath deletedBy = createString("deletedBy");

    public final DateTimePath<java.time.LocalDateTime> deletedDate = createDateTime("deletedDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> deletedEmpId = createNumber("deletedEmpId", Long.class);

    public final StringPath deletedReason = createString("deletedReason");

    public final com.bizmate.hr.domain.QDepartment department;

    public final MapPath<String, Object, SimplePath<Object>> docContent = this.<String, Object, SimplePath<Object>>createMap("docContent", String.class, Object.class, SimplePath.class);

    public final StringPath docId = createString("docId");

    public final EnumPath<DocumentType> docType = createEnum("docType", DocumentType.class);

    public final StringPath finalDocNumber = createString("finalDocNumber");

    public final StringPath rejectedBy = createString("rejectedBy");

    public final DateTimePath<java.time.LocalDateTime> rejectedDate = createDateTime("rejectedDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> rejectedEmpId = createNumber("rejectedEmpId", Long.class);

    public final StringPath rejectedReason = createString("rejectedReason");

    public final EnumPath<DocumentStatus> status = createEnum("status", DocumentStatus.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final ListPath<String, StringPath> viewerIds = this.<String, StringPath>createList("viewerIds", String.class, StringPath.class, PathInits.DIRECT2);

    public QApprovalDocuments(String variable) {
        this(ApprovalDocuments.class, forVariable(variable), INITS);
    }

    public QApprovalDocuments(Path<? extends ApprovalDocuments> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApprovalDocuments(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApprovalDocuments(PathMetadata metadata, PathInits inits) {
        this(ApprovalDocuments.class, metadata, inits);
    }

    public QApprovalDocuments(Class<? extends ApprovalDocuments> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.authorEmployee = inits.isInitialized("authorEmployee") ? new com.bizmate.hr.domain.QEmployee(forProperty("authorEmployee"), inits.get("authorEmployee")) : null;
        this.authorRole = inits.isInitialized("authorRole") ? new com.bizmate.hr.domain.QRole(forProperty("authorRole")) : null;
        this.authorUser = inits.isInitialized("authorUser") ? new com.bizmate.hr.domain.QUserEntity(forProperty("authorUser"), inits.get("authorUser")) : null;
        this.department = inits.isInitialized("department") ? new com.bizmate.hr.domain.QDepartment(forProperty("department"), inits.get("department")) : null;
    }

}

