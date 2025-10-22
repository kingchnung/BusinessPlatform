package com.bizmate.hr.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAssignmentsHistory is a Querydsl query type for AssignmentsHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAssignmentsHistory extends EntityPathBase<AssignmentsHistory> {

    private static final long serialVersionUID = -378860553L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAssignmentsHistory assignmentsHistory = new QAssignmentsHistory("assignmentsHistory");

    public final DatePath<java.time.LocalDate> assDate = createDate("assDate", java.time.LocalDate.class);

    public final NumberPath<Long> assId = createNumber("assId", Long.class);

    public final QUserEntity createdBy;

    public final DateTimePath<java.time.LocalDateTime> creDate = createDateTime("creDate", java.time.LocalDateTime.class);

    public final QEmployee employee;

    public final QDepartment newDepartment;

    public final com.bizmate.hr.domain.code.QGrade newGrade;

    public final com.bizmate.hr.domain.code.QPosition newPosition;

    public final QDepartment previousDepartment;

    public final com.bizmate.hr.domain.code.QGrade previousGrade;

    public final com.bizmate.hr.domain.code.QPosition previousPosition;

    public final StringPath reason = createString("reason");

    public QAssignmentsHistory(String variable) {
        this(AssignmentsHistory.class, forVariable(variable), INITS);
    }

    public QAssignmentsHistory(Path<? extends AssignmentsHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAssignmentsHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAssignmentsHistory(PathMetadata metadata, PathInits inits) {
        this(AssignmentsHistory.class, metadata, inits);
    }

    public QAssignmentsHistory(Class<? extends AssignmentsHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createdBy = inits.isInitialized("createdBy") ? new QUserEntity(forProperty("createdBy"), inits.get("createdBy")) : null;
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee"), inits.get("employee")) : null;
        this.newDepartment = inits.isInitialized("newDepartment") ? new QDepartment(forProperty("newDepartment"), inits.get("newDepartment")) : null;
        this.newGrade = inits.isInitialized("newGrade") ? new com.bizmate.hr.domain.code.QGrade(forProperty("newGrade")) : null;
        this.newPosition = inits.isInitialized("newPosition") ? new com.bizmate.hr.domain.code.QPosition(forProperty("newPosition")) : null;
        this.previousDepartment = inits.isInitialized("previousDepartment") ? new QDepartment(forProperty("previousDepartment"), inits.get("previousDepartment")) : null;
        this.previousGrade = inits.isInitialized("previousGrade") ? new com.bizmate.hr.domain.code.QGrade(forProperty("previousGrade")) : null;
        this.previousPosition = inits.isInitialized("previousPosition") ? new com.bizmate.hr.domain.code.QPosition(forProperty("previousPosition")) : null;
    }

}

