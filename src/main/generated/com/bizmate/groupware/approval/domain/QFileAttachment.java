package com.bizmate.groupware.approval.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFileAttachment is a Querydsl query type for FileAttachment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileAttachment extends EntityPathBase<FileAttachment> {

    private static final long serialVersionUID = 1833490163L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFileAttachment fileAttachment = new QFileAttachment("fileAttachment");

    public final StringPath contentType = createString("contentType");

    public final QApprovalDocuments document;

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originalName = createString("originalName");

    public final StringPath storedName = createString("storedName");

    public final DateTimePath<java.time.LocalDateTime> uploadedAt = createDateTime("uploadedAt", java.time.LocalDateTime.class);

    public final com.bizmate.hr.domain.QUserEntity uploader;

    public QFileAttachment(String variable) {
        this(FileAttachment.class, forVariable(variable), INITS);
    }

    public QFileAttachment(Path<? extends FileAttachment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFileAttachment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFileAttachment(PathMetadata metadata, PathInits inits) {
        this(FileAttachment.class, metadata, inits);
    }

    public QFileAttachment(Class<? extends FileAttachment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.document = inits.isInitialized("document") ? new QApprovalDocuments(forProperty("document"), inits.get("document")) : null;
        this.uploader = inits.isInitialized("uploader") ? new com.bizmate.hr.domain.QUserEntity(forProperty("uploader"), inits.get("uploader")) : null;
    }

}

