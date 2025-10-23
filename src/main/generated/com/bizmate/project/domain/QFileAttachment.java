package com.bizmate.project.domain;

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

    private static final long serialVersionUID = -1260915279L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFileAttachment fileAttachment = new QFileAttachment("fileAttachment");

    public final com.bizmate.project.domain.auditings.QBaseTimeEntity _super = new com.bizmate.project.domain.auditings.QBaseTimeEntity(this);

    public final NumberPath<Long> fileId = createNumber("fileId", Long.class);

    public final StringPath filePath = createString("filePath");

    public final QFilePost filePost;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final StringPath modId = _super.modId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    //inherited
    public final StringPath regId = _super.regId;

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
        this.filePost = inits.isInitialized("filePost") ? new QFilePost(forProperty("filePost"), inits.get("filePost")) : null;
    }

}

