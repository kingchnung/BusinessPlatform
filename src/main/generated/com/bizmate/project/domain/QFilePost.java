package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFilePost is a Querydsl query type for FilePost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFilePost extends EntityPathBase<FilePost> {

    private static final long serialVersionUID = -743360594L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFilePost filePost = new QFilePost("filePost");

    public final com.bizmate.project.domain.auditings.QBaseTimeEntity _super = new com.bizmate.project.domain.auditings.QBaseTimeEntity(this);

    public final NumberPath<Long> filePostId = createNumber("filePostId", Long.class);

    public final StringPath fpContent = createString("fpContent");

    public final StringPath fpTitle = createString("fpTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final StringPath modId = _super.modId;

    public final QProjectBoard projectBoard;

    public final QProjectMember projectMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    //inherited
    public final StringPath regId = _super.regId;

    public QFilePost(String variable) {
        this(FilePost.class, forVariable(variable), INITS);
    }

    public QFilePost(Path<? extends FilePost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFilePost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFilePost(PathMetadata metadata, PathInits inits) {
        this(FilePost.class, metadata, inits);
    }

    public QFilePost(Class<? extends FilePost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.projectBoard = inits.isInitialized("projectBoard") ? new QProjectBoard(forProperty("projectBoard"), inits.get("projectBoard")) : null;
        this.projectMember = inits.isInitialized("projectMember") ? new QProjectMember(forProperty("projectMember"), inits.get("projectMember")) : null;
    }

}

