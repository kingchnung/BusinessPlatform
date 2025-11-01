package com.bizmate.salesPages.management.collections.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCollection is a Querydsl query type for Collection
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCollection extends EntityPathBase<Collection> {

    private static final long serialVersionUID = -2034475657L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCollection collection = new QCollection("collection");

    public final com.bizmate.salesPages.client.domain.QClient client;

    public final DatePath<java.time.LocalDate> collectionDate = createDate("collectionDate", java.time.LocalDate.class);

    public final StringPath collectionId = createString("collectionId");

    public final NumberPath<java.math.BigDecimal> collectionMoney = createNumber("collectionMoney", java.math.BigDecimal.class);

    public final NumberPath<Long> collectionNo = createNumber("collectionNo", Long.class);

    public final StringPath collectionNote = createString("collectionNote");

    public final StringPath userId = createString("userId");

    public final StringPath writer = createString("writer");

    public QCollection(String variable) {
        this(Collection.class, forVariable(variable), INITS);
    }

    public QCollection(Path<? extends Collection> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCollection(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCollection(PathMetadata metadata, PathInits inits) {
        this(Collection.class, metadata, inits);
    }

    public QCollection(Class<? extends Collection> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.client = inits.isInitialized("client") ? new com.bizmate.salesPages.client.domain.QClient(forProperty("client")) : null;
    }

}

