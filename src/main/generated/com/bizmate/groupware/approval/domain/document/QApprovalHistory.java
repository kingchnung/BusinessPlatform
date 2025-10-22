package com.bizmate.groupware.approval.domain.document;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QApprovalHistory is a Querydsl query type for ApprovalHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApprovalHistory extends EntityPathBase<ApprovalHistory> {

    private static final long serialVersionUID = 1529718770L;

    public static final QApprovalHistory approvalHistory = new QApprovalHistory("approvalHistory");

    public final StringPath actionComment = createString("actionComment");

    public final DateTimePath<java.time.LocalDateTime> actionTimestamp = createDateTime("actionTimestamp", java.time.LocalDateTime.class);

    public final StringPath actionType = createString("actionType");

    public final NumberPath<Long> actorUserId = createNumber("actorUserId", Long.class);

    public final StringPath docId = createString("docId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QApprovalHistory(String variable) {
        super(ApprovalHistory.class, forVariable(variable));
    }

    public QApprovalHistory(Path<? extends ApprovalHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApprovalHistory(PathMetadata metadata) {
        super(ApprovalHistory.class, metadata);
    }

}

