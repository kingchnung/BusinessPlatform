package com.bizmate.groupware.board.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentHistory is a Querydsl query type for CommentHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentHistory extends EntityPathBase<CommentHistory> {

    private static final long serialVersionUID = 1760357570L;

    public static final QCommentHistory commentHistory = new QCommentHistory("commentHistory");

    public final StringPath actionComment = createString("actionComment");

    public final DateTimePath<java.time.LocalDateTime> actionTimestamp = createDateTime("actionTimestamp", java.time.LocalDateTime.class);

    public final StringPath actionType = createString("actionType");

    public final StringPath actorUserId = createString("actorUserId");

    public final NumberPath<Long> boardNo = createNumber("boardNo", Long.class);

    public final NumberPath<Long> commentNo = createNumber("commentNo", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QCommentHistory(String variable) {
        super(CommentHistory.class, forVariable(variable));
    }

    public QCommentHistory(Path<? extends CommentHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentHistory(PathMetadata metadata) {
        super(CommentHistory.class, metadata);
    }

}

