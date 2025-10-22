package com.bizmate.groupware.board.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoardHistory is a Querydsl query type for BoardHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardHistory extends EntityPathBase<BoardHistory> {

    private static final long serialVersionUID = 498800027L;

    public static final QBoardHistory boardHistory = new QBoardHistory("boardHistory");

    public final StringPath actionComment = createString("actionComment");

    public final DateTimePath<java.time.LocalDateTime> actionTimestamp = createDateTime("actionTimestamp", java.time.LocalDateTime.class);

    public final StringPath actionType = createString("actionType");

    public final StringPath actorUserId = createString("actorUserId");

    public final NumberPath<Long> boardNo = createNumber("boardNo", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBoardHistory(String variable) {
        super(BoardHistory.class, forVariable(variable));
    }

    public QBoardHistory(Path<? extends BoardHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoardHistory(PathMetadata metadata) {
        super(BoardHistory.class, metadata);
    }

}

