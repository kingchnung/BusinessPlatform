package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectMember is a Querydsl query type for ProjectMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectMember extends EntityPathBase<ProjectMember> {

    private static final long serialVersionUID = 873025249L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectMember projectMember = new QProjectMember("projectMember");

    public final com.bizmate.project.domain.auditings.QBaseTimeEntity _super = new com.bizmate.project.domain.auditings.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final StringPath modId = _super.modId;

    public final com.bizmate.project.domain.embeddables.QProjectMemberId pmId;

    public final EnumPath<com.bizmate.project.domain.enums.ProjectMemberStatus> pmRoleName = createEnum("pmRoleName", com.bizmate.project.domain.enums.ProjectMemberStatus.class);

    public final QProject projectId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    //inherited
    public final StringPath regId = _super.regId;

    public final QTask task;

    public final com.bizmate.hr.domain.QUserEntity userId;

    public QProjectMember(String variable) {
        this(ProjectMember.class, forVariable(variable), INITS);
    }

    public QProjectMember(Path<? extends ProjectMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectMember(PathMetadata metadata, PathInits inits) {
        this(ProjectMember.class, metadata, inits);
    }

    public QProjectMember(Class<? extends ProjectMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pmId = inits.isInitialized("pmId") ? new com.bizmate.project.domain.embeddables.QProjectMemberId(forProperty("pmId")) : null;
        this.projectId = inits.isInitialized("projectId") ? new QProject(forProperty("projectId"), inits.get("projectId")) : null;
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task")) : null;
        this.userId = inits.isInitialized("userId") ? new com.bizmate.hr.domain.QUserEntity(forProperty("userId"), inits.get("userId")) : null;
    }

}

