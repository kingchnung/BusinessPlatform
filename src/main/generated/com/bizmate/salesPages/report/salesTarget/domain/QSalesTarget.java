package com.bizmate.salesPages.report.salesTarget.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSalesTarget is a Querydsl query type for SalesTarget
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSalesTarget extends EntityPathBase<SalesTarget> {

    private static final long serialVersionUID = -1641552821L;

    public static final QSalesTarget salesTarget = new QSalesTarget("salesTarget");

    public final DatePath<java.time.LocalDate> registrationDate = createDate("registrationDate", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> targetAmount = createNumber("targetAmount", java.math.BigDecimal.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final NumberPath<Integer> targetMonth = createNumber("targetMonth", Integer.class);

    public final NumberPath<Integer> targetYear = createNumber("targetYear", Integer.class);

    public final StringPath userId = createString("userId");

    public final StringPath writer = createString("writer");

    public QSalesTarget(String variable) {
        super(SalesTarget.class, forVariable(variable));
    }

    public QSalesTarget(Path<? extends SalesTarget> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSalesTarget(PathMetadata metadata) {
        super(SalesTarget.class, metadata);
    }

}

