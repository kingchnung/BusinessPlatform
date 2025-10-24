package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectBudgetItem is a Querydsl query type for ProjectBudgetItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectBudgetItem extends EntityPathBase<ProjectBudgetItem> {

    private static final long serialVersionUID = -2046313281L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectBudgetItem projectBudgetItem = new QProjectBudgetItem("projectBudgetItem");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    public final StringPath itemName = createString("itemName");

    public final QProject project;

    public QProjectBudgetItem(String variable) {
        this(ProjectBudgetItem.class, forVariable(variable), INITS);
    }

    public QProjectBudgetItem(Path<? extends ProjectBudgetItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectBudgetItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectBudgetItem(PathMetadata metadata, PathInits inits) {
        this(ProjectBudgetItem.class, metadata, inits);
    }

    public QProjectBudgetItem(Class<? extends ProjectBudgetItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

