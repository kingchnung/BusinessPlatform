package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 244629863L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final com.bizmate.project.domain.auditings.QBaseTimeEntity _super = new com.bizmate.project.domain.auditings.QBaseTimeEntity(this);

    public final com.bizmate.groupware.approval.domain.document.QApprovalDocuments approvalDocument;

    public final com.bizmate.salesPages.client.domain.QClient clientId;

    public final StringPath managerName = createString("managerName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final StringPath modId = _super.modId;

    public final NumberPath<Long> projectBudget = createNumber("projectBudget", Long.class);

    public final DateTimePath<java.time.LocalDateTime> projectEndDate = createDateTime("projectEndDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    public final EnumPath<com.bizmate.project.domain.enums.project.ProjectImportance> projectImportance = createEnum("projectImportance", com.bizmate.project.domain.enums.project.ProjectImportance.class);

    public final StringPath projectName = createString("projectName");

    public final StringPath projectNo = createString("projectNo");

    public final DateTimePath<java.time.LocalDateTime> projectStartDate = createDateTime("projectStartDate", java.time.LocalDateTime.class);

    public final EnumPath<com.bizmate.project.domain.enums.project.ProjectStatus> projectStatus = createEnum("projectStatus", com.bizmate.project.domain.enums.project.ProjectStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    //inherited
    public final StringPath regId = _super.regId;

    public final com.bizmate.hr.domain.QUserEntity userId;

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.approvalDocument = inits.isInitialized("approvalDocument") ? new com.bizmate.groupware.approval.domain.document.QApprovalDocuments(forProperty("approvalDocument"), inits.get("approvalDocument")) : null;
        this.clientId = inits.isInitialized("clientId") ? new com.bizmate.salesPages.client.domain.QClient(forProperty("clientId")) : null;
        this.userId = inits.isInitialized("userId") ? new com.bizmate.hr.domain.QUserEntity(forProperty("userId"), inits.get("userId")) : null;
    }

}

