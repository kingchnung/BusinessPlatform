package com.bizmate.groupware.approval.domain.PDF;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmployeeSignature is a Querydsl query type for EmployeeSignature
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmployeeSignature extends EntityPathBase<EmployeeSignature> {

    private static final long serialVersionUID = 125456858L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmployeeSignature employeeSignature = new QEmployeeSignature("employeeSignature");

    public final com.bizmate.hr.domain.QEmployee employee;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath signImagePath = createString("signImagePath");

    public QEmployeeSignature(String variable) {
        this(EmployeeSignature.class, forVariable(variable), INITS);
    }

    public QEmployeeSignature(Path<? extends EmployeeSignature> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmployeeSignature(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmployeeSignature(PathMetadata metadata, PathInits inits) {
        this(EmployeeSignature.class, metadata, inits);
    }

    public QEmployeeSignature(Class<? extends EmployeeSignature> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new com.bizmate.hr.domain.QEmployee(forProperty("employee"), inits.get("employee")) : null;
    }

}

