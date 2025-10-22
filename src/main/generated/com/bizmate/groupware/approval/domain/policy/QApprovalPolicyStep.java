package com.bizmate.groupware.approval.domain.policy;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApprovalPolicyStep is a Querydsl query type for ApprovalPolicyStep
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalPolicyStep extends EntityPathBase<ApprovalPolicyStep> {

    private static final long serialVersionUID = 446474377L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApprovalPolicyStep approvalPolicyStep = new QApprovalPolicyStep("approvalPolicyStep");

    public final com.bizmate.hr.domain.QEmployee approver;

    public final StringPath deptCode = createString("deptCode");

    public final StringPath deptName = createString("deptName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QApprovalPolicy policy;

    public final StringPath positionCode = createString("positionCode");

    public final StringPath positionName = createString("positionName");

    public final NumberPath<Integer> stepOrder = createNumber("stepOrder", Integer.class);

    public QApprovalPolicyStep(String variable) {
        this(ApprovalPolicyStep.class, forVariable(variable), INITS);
    }

    public QApprovalPolicyStep(Path<? extends ApprovalPolicyStep> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApprovalPolicyStep(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApprovalPolicyStep(PathMetadata metadata, PathInits inits) {
        this(ApprovalPolicyStep.class, metadata, inits);
    }

    public QApprovalPolicyStep(Class<? extends ApprovalPolicyStep> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.approver = inits.isInitialized("approver") ? new com.bizmate.hr.domain.QEmployee(forProperty("approver"), inits.get("approver")) : null;
        this.policy = inits.isInitialized("policy") ? new QApprovalPolicy(forProperty("policy")) : null;
    }

}

