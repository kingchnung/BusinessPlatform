package com.bizmate.hr.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmployee is a Querydsl query type for Employee
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmployee extends EntityPathBase<Employee> {

    private static final long serialVersionUID = 1829276535L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmployee employee = new QEmployee("employee");

    public final StringPath address = createString("address");

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final NumberPath<Double> careerYears = createNumber("careerYears", Double.class);

    public final QUserEntity creBy;

    public final DateTimePath<java.time.LocalDateTime> creDate = createDateTime("creDate", java.time.LocalDateTime.class);

    public final QDepartment department;

    public final StringPath email = createString("email");

    public final NumberPath<Long> empId = createNumber("empId", Long.class);

    public final StringPath empName = createString("empName");

    public final StringPath empNo = createString("empNo");

    public final StringPath gender = createString("gender");

    public final com.bizmate.hr.domain.code.QGrade grade;

    public final DatePath<java.time.LocalDate> leaveDate = createDate("leaveDate", java.time.LocalDate.class);

    public final StringPath phone = createString("phone");

    public final com.bizmate.hr.domain.code.QPosition position;

    public final StringPath ssnMask = createString("ssnMask");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final StringPath status = createString("status");

    public final QUserEntity updBy;

    public final DateTimePath<java.time.LocalDateTime> updDate = createDateTime("updDate", java.time.LocalDateTime.class);

    public QEmployee(String variable) {
        this(Employee.class, forVariable(variable), INITS);
    }

    public QEmployee(Path<? extends Employee> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmployee(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmployee(PathMetadata metadata, PathInits inits) {
        this(Employee.class, metadata, inits);
    }

    public QEmployee(Class<? extends Employee> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.creBy = inits.isInitialized("creBy") ? new QUserEntity(forProperty("creBy"), inits.get("creBy")) : null;
        this.department = inits.isInitialized("department") ? new QDepartment(forProperty("department"), inits.get("department")) : null;
        this.grade = inits.isInitialized("grade") ? new com.bizmate.hr.domain.code.QGrade(forProperty("grade")) : null;
        this.position = inits.isInitialized("position") ? new com.bizmate.hr.domain.code.QPosition(forProperty("position")) : null;
        this.updBy = inits.isInitialized("updBy") ? new QUserEntity(forProperty("updBy"), inits.get("updBy")) : null;
    }

}

