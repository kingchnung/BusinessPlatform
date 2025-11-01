package com.bizmate.hr.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDepartment is a Querydsl query type for Department
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDepartment extends EntityPathBase<Department> {

    private static final long serialVersionUID = 1973279195L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDepartment department = new QDepartment("department");

    public final ListPath<Department, QDepartment> childDepts = this.<Department, QDepartment>createList("childDepts", Department.class, QDepartment.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> creDate = createDateTime("creDate", java.time.LocalDateTime.class);

    public final StringPath deptCode = createString("deptCode");

    public final NumberPath<Long> deptId = createNumber("deptId", Long.class);

    public final StringPath deptName = createString("deptName");

    public final ListPath<Employee, QEmployee> employees = this.<Employee, QEmployee>createList("employees", Employee.class, QEmployee.class, PathInits.DIRECT2);

    public final StringPath isUsed = createString("isUsed");

    public final QEmployee manager;

    public final QDepartment parentDept;

    public final DateTimePath<java.time.LocalDateTime> updDate = createDateTime("updDate", java.time.LocalDateTime.class);

    public QDepartment(String variable) {
        this(Department.class, forVariable(variable), INITS);
    }

    public QDepartment(Path<? extends Department> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDepartment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDepartment(PathMetadata metadata, PathInits inits) {
        this(Department.class, metadata, inits);
    }

    public QDepartment(Class<? extends Department> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.manager = inits.isInitialized("manager") ? new QEmployee(forProperty("manager"), inits.get("manager")) : null;
        this.parentDept = inits.isInitialized("parentDept") ? new QDepartment(forProperty("parentDept"), inits.get("parentDept")) : null;
    }

}

