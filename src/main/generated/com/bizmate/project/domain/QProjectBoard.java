package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectBoard is a Querydsl query type for ProjectBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectBoard extends EntityPathBase<ProjectBoard> {

    private static final long serialVersionUID = -120257089L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectBoard projectBoard = new QProjectBoard("projectBoard");

    public final com.bizmate.project.domain.auditings.QBaseTimeEntity _super = new com.bizmate.project.domain.auditings.QBaseTimeEntity(this);

    public final StringPath boardTitle = createString("boardTitle");

    public final EnumPath<com.bizmate.project.domain.enums.ProjectBoardStatus> boardType = createEnum("boardType", com.bizmate.project.domain.enums.ProjectBoardStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final StringPath modId = _super.modId;

    public final NumberPath<Long> projectBoardId = createNumber("projectBoardId", Long.class);

    public final QProject projectId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    //inherited
    public final StringPath regId = _super.regId;

    public QProjectBoard(String variable) {
        this(ProjectBoard.class, forVariable(variable), INITS);
    }

    public QProjectBoard(Path<? extends ProjectBoard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectBoard(PathMetadata metadata, PathInits inits) {
        this(ProjectBoard.class, metadata, inits);
    }

    public QProjectBoard(Class<? extends ProjectBoard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.projectId = inits.isInitialized("projectId") ? new QProject(forProperty("projectId"), inits.get("projectId")) : null;
    }

}

