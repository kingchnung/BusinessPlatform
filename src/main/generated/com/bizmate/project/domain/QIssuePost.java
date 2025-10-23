package com.bizmate.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIssuePost is a Querydsl query type for IssuePost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssuePost extends EntityPathBase<IssuePost> {

    private static final long serialVersionUID = -54412313L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIssuePost issuePost = new QIssuePost("issuePost");

    public final com.bizmate.project.domain.auditings.QBaseTimeEntity _super = new com.bizmate.project.domain.auditings.QBaseTimeEntity(this);

    public final StringPath ipContent = createString("ipContent");

    public final StringPath ipTitle = createString("ipTitle");

    public final NumberPath<Integer> issuePostId = createNumber("issuePostId", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final StringPath modId = _super.modId;

    public final QProjectBoard projectBoard;

    public final QProjectMember projectMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    //inherited
    public final StringPath regId = _super.regId;

    public QIssuePost(String variable) {
        this(IssuePost.class, forVariable(variable), INITS);
    }

    public QIssuePost(Path<? extends IssuePost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIssuePost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIssuePost(PathMetadata metadata, PathInits inits) {
        this(IssuePost.class, metadata, inits);
    }

    public QIssuePost(Class<? extends IssuePost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.projectBoard = inits.isInitialized("projectBoard") ? new QProjectBoard(forProperty("projectBoard"), inits.get("projectBoard")) : null;
        this.projectMember = inits.isInitialized("projectMember") ? new QProjectMember(forProperty("projectMember"), inits.get("projectMember")) : null;
    }

}

