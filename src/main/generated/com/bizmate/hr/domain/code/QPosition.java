package com.bizmate.hr.domain.code;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPosition is a Querydsl query type for Position
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosition extends EntityPathBase<Position> {

    private static final long serialVersionUID = -1256563423L;

    public static final QPosition position = new QPosition("position1");

    public final StringPath description = createString("description");

    public final StringPath isUsed = createString("isUsed");

    public final NumberPath<Long> positionCode = createNumber("positionCode", Long.class);

    public final StringPath positionName = createString("positionName");

    public QPosition(String variable) {
        super(Position.class, forVariable(variable));
    }

    public QPosition(Path<? extends Position> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPosition(PathMetadata metadata) {
        super(Position.class, metadata);
    }

}

