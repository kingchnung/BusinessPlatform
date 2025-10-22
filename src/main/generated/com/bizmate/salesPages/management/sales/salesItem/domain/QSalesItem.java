package com.bizmate.salesPages.management.sales.salesItem.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSalesItem is a Querydsl query type for SalesItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSalesItem extends EntityPathBase<SalesItem> {

    private static final long serialVersionUID = 1207650046L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSalesItem salesItem = new QSalesItem("salesItem");

    public final StringPath itemName = createString("itemName");

    public final StringPath itemNote = createString("itemNote");

    public final NumberPath<Integer> lineNum = createNumber("lineNum", Integer.class);

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final com.bizmate.salesPages.management.sales.sales.domain.QSales sales;

    public final NumberPath<Long> salesItemId = createNumber("salesItemId", Long.class);

    public final NumberPath<java.math.BigDecimal> totalAmount = createNumber("totalAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> unitPrice = createNumber("unitPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> unitVat = createNumber("unitVat", java.math.BigDecimal.class);

    public QSalesItem(String variable) {
        this(SalesItem.class, forVariable(variable), INITS);
    }

    public QSalesItem(Path<? extends SalesItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSalesItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSalesItem(PathMetadata metadata, PathInits inits) {
        this(SalesItem.class, metadata, inits);
    }

    public QSalesItem(Class<? extends SalesItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sales = inits.isInitialized("sales") ? new com.bizmate.salesPages.management.sales.sales.domain.QSales(forProperty("sales"), inits.get("sales")) : null;
    }

}

