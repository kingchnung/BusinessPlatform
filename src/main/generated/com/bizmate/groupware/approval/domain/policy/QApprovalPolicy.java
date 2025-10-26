package com.bizmate.groupware.approval.domain.policy;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApprovalPolicy is a Querydsl query type for ApprovalPolicy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalPolicy extends EntityPathBase<ApprovalPolicy> {

    private static final long serialVersionUID = -474053603L;

    public static final QApprovalPolicy approvalPolicy = new QApprovalPolicy("approvalPolicy");

    public final com.bizmate.common.domain.QBaseEntity _super = new com.bizmate.common.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath createdBy = createString("createdBy");

    public final StringPath createdDept = createString("createdDept");

    public final EnumPath<com.bizmate.groupware.approval.domain.document.DocumentType> docType = createEnum("docType", com.bizmate.groupware.approval.domain.document.DocumentType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath policyName = createString("policyName");

    public final ListPath<ApprovalPolicyStep, QApprovalPolicyStep> steps = this.<ApprovalPolicyStep, QApprovalPolicyStep>createList("steps", ApprovalPolicyStep.class, QApprovalPolicyStep.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QApprovalPolicy(String variable) {
        super(ApprovalPolicy.class, forVariable(variable));
    }

    public QApprovalPolicy(Path<? extends ApprovalPolicy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApprovalPolicy(PathMetadata metadata) {
        super(ApprovalPolicy.class, metadata);
    }

}

