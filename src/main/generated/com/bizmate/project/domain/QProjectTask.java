package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectTask is a Querydsl query type for ProjectTask
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectTask extends EntityPathBase<ProjectTask> {

    private static final long serialVersionUID = 1243570060L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectTask projectTask = new QProjectTask("projectTask");

    public final QProjectMember assigneeId;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final EnumPath<com.bizmate.project.domain.enums.task.TaskPriority> priority = createEnum("priority", com.bizmate.project.domain.enums.task.TaskPriority.class);

    public final NumberPath<Integer> progressRate = createNumber("progressRate", Integer.class);

    public final QProject project;

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<com.bizmate.project.domain.enums.task.TaskStatus> status = createEnum("status", com.bizmate.project.domain.enums.task.TaskStatus.class);

    public final StringPath taskDescription = createString("taskDescription");

    public final NumberPath<Long> taskId = createNumber("taskId", Long.class);

    public final StringPath taskName = createString("taskName");

    public QProjectTask(String variable) {
        this(ProjectTask.class, forVariable(variable), INITS);
    }

    public QProjectTask(Path<? extends ProjectTask> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectTask(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectTask(PathMetadata metadata, PathInits inits) {
        this(ProjectTask.class, metadata, inits);
    }

    public QProjectTask(Class<? extends ProjectTask> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.assigneeId = inits.isInitialized("assigneeId") ? new QProjectMember(forProperty("assigneeId"), inits.get("assigneeId")) : null;
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

