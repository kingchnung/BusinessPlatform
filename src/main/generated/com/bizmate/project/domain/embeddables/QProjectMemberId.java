package com.bizmate.project.domain.embeddables;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProjectMemberId is a Querydsl query type for ProjectMemberId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QProjectMemberId extends BeanPath<ProjectMemberId> {

    private static final long serialVersionUID = -58806916L;

    public static final QProjectMemberId projectMemberId = new QProjectMemberId("projectMemberId");

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QProjectMemberId(String variable) {
        super(ProjectMemberId.class, forVariable(variable));
    }

    public QProjectMemberId(Path<? extends ProjectMemberId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProjectMemberId(PathMetadata metadata) {
        super(ProjectMemberId.class, metadata);
    }

}

