package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTask is a Querydsl query type for Task
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTask extends EntityPathBase<Task> {

    private static final long serialVersionUID = 231215607L;

    public static final QTask task = new QTask("task");

    public final EnumPath<com.bizmate.project.domain.enums.task.TaskPriority> priority = createEnum("priority", com.bizmate.project.domain.enums.task.TaskPriority.class);

    public final EnumPath<com.bizmate.project.domain.enums.task.TaskStatus> status = createEnum("status", com.bizmate.project.domain.enums.task.TaskStatus.class);

    public final DateTimePath<java.time.LocalDateTime> taskEndDate = createDateTime("taskEndDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> taskId = createNumber("taskId", Long.class);

    public final StringPath taskName = createString("taskName");

    public final DateTimePath<java.time.LocalDateTime> taskStartDate = createDateTime("taskStartDate", java.time.LocalDateTime.class);

    public QTask(String variable) {
        super(Task.class, forVariable(variable));
    }

    public QTask(Path<? extends Task> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTask(PathMetadata metadata) {
        super(Task.class, metadata);
    }

}

