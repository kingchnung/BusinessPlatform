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

    public final com.bizmate.common.domain.QBaseEntity _super = new com.bizmate.common.domain.QBaseEntity(this);

    public final com.bizmate.groupware.approval.domain.document.QApprovalDocuments approvalDocument;

    public final com.bizmate.hr.domain.QUserEntity author;

    public final ListPath<ProjectBudgetItem, QProjectBudgetItem> budgetItems = this.<ProjectBudgetItem, QProjectBudgetItem>createList("budgetItems", ProjectBudgetItem.class, QProjectBudgetItem.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final com.bizmate.hr.domain.QDepartment department;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final StringPath expectedEffect = createString("expectedEffect");

    public final ListPath<ProjectMember, QProjectMember> participants = this.<ProjectMember, QProjectMember>createList("participants", ProjectMember.class, QProjectMember.class, PathInits.DIRECT2);

    public final StringPath projectGoal = createString("projectGoal");

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    public final StringPath projectName = createString("projectName");

    public final StringPath projectOverview = createString("projectOverview");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<com.bizmate.project.domain.enums.project.ProjectStatus> status = createEnum("status", com.bizmate.project.domain.enums.project.ProjectStatus.class);

    public final ListPath<ProjectTask, QProjectTask> tasks = this.<ProjectTask, QProjectTask>createList("tasks", ProjectTask.class, QProjectTask.class, PathInits.DIRECT2);

    public final NumberPath<Long> totalBudget = createNumber("totalBudget", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

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
        this.author = inits.isInitialized("author") ? new com.bizmate.hr.domain.QUserEntity(forProperty("author"), inits.get("author")) : null;
        this.department = inits.isInitialized("department") ? new com.bizmate.hr.domain.QDepartment(forProperty("department"), inits.get("department")) : null;
    }

}

