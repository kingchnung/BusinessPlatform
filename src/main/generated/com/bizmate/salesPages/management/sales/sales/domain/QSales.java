package com.bizmate.salesPages.management.sales.sales.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSales is a Querydsl query type for Sales
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSales extends EntityPathBase<Sales> {

    private static final long serialVersionUID = -971497314L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSales sales = new QSales("sales");

    public final StringPath clientCompany = createString("clientCompany");

    public final StringPath clientId = createString("clientId");

    public final DatePath<java.time.LocalDate> deploymentDate = createDate("deploymentDate", java.time.LocalDate.class);

    public final BooleanPath invoiceIssued = createBoolean("invoiceIssued");

    public final com.bizmate.salesPages.management.order.order.domain.QOrder order;

    public final StringPath projectId = createString("projectId");

    public final StringPath projectName = createString("projectName");

    public final NumberPath<java.math.BigDecimal> salesAmount = createNumber("salesAmount", java.math.BigDecimal.class);

    public final DatePath<java.time.LocalDate> salesDate = createDate("salesDate", java.time.LocalDate.class);

    public final StringPath salesId = createString("salesId");

    public final DatePath<java.time.LocalDate> salesIdDate = createDate("salesIdDate", java.time.LocalDate.class);

    public final ListPath<com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem, com.bizmate.salesPages.management.sales.salesItem.domain.QSalesItem> salesItems = this.<com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem, com.bizmate.salesPages.management.sales.salesItem.domain.QSalesItem>createList("salesItems", com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem.class, com.bizmate.salesPages.management.sales.salesItem.domain.QSalesItem.class, PathInits.DIRECT2);

    public final NumberPath<Long> salesNo = createNumber("salesNo", Long.class);

    public final StringPath salesNote = createString("salesNote");

    public final NumberPath<java.math.BigDecimal> totalSubAmount = createNumber("totalSubAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalVatAmount = createNumber("totalVatAmount", java.math.BigDecimal.class);

    public final StringPath userId = createString("userId");

    public final StringPath writer = createString("writer");

    public QSales(String variable) {
        this(Sales.class, forVariable(variable), INITS);
    }

    public QSales(Path<? extends Sales> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSales(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSales(PathMetadata metadata, PathInits inits) {
        this(Sales.class, metadata, inits);
    }

    public QSales(Class<? extends Sales> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new com.bizmate.salesPages.management.order.order.domain.QOrder(forProperty("order")) : null;
    }

}

