package com.bizmate.groupware.approval.domain.attachment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApprovalFileAttachment is a Querydsl query type for ApprovalFileAttachment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalFileAttachment extends EntityPathBase<ApprovalFileAttachment> {

    private static final long serialVersionUID = 341285529L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApprovalFileAttachment approvalFileAttachment = new QApprovalFileAttachment("approvalFileAttachment");

    public final StringPath contentType = createString("contentType");

    public final com.bizmate.groupware.approval.domain.document.QApprovalDocuments document;

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originalName = createString("originalName");

    public final StringPath storedName = createString("storedName");

    public final DateTimePath<java.time.LocalDateTime> uploadedAt = createDateTime("uploadedAt", java.time.LocalDateTime.class);

    public final com.bizmate.hr.domain.QUserEntity uploader;

    public QApprovalFileAttachment(String variable) {
        this(ApprovalFileAttachment.class, forVariable(variable), INITS);
    }

    public QApprovalFileAttachment(Path<? extends ApprovalFileAttachment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApprovalFileAttachment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApprovalFileAttachment(PathMetadata metadata, PathInits inits) {
        this(ApprovalFileAttachment.class, metadata, inits);
    }

    public QApprovalFileAttachment(Class<? extends ApprovalFileAttachment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.document = inits.isInitialized("document") ? new com.bizmate.groupware.approval.domain.document.QApprovalDocuments(forProperty("document"), inits.get("document")) : null;
        this.uploader = inits.isInitialized("uploader") ? new com.bizmate.hr.domain.QUserEntity(forProperty("uploader"), inits.get("uploader")) : null;
    }

}

