package com.bizmate.salesPages.management.order.order.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 32514652L;

    public static final QOrder order = new QOrder("order1");

    public final StringPath clientCompany = createString("clientCompany");

    public final StringPath clientId = createString("clientId");

    public final NumberPath<java.math.BigDecimal> orderAmount = createNumber("orderAmount", java.math.BigDecimal.class);

    public final DatePath<java.time.LocalDate> orderDate = createDate("orderDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> orderDueDate = createDate("orderDueDate", java.time.LocalDate.class);

    public final StringPath orderId = createString("orderId");

    public final DatePath<java.time.LocalDate> orderIdDate = createDate("orderIdDate", java.time.LocalDate.class);

    public final ListPath<com.bizmate.salesPages.management.order.orderItem.domain.OrderItem, com.bizmate.salesPages.management.order.orderItem.domain.QOrderItem> orderItems = this.<com.bizmate.salesPages.management.order.orderItem.domain.OrderItem, com.bizmate.salesPages.management.order.orderItem.domain.QOrderItem>createList("orderItems", com.bizmate.salesPages.management.order.orderItem.domain.OrderItem.class, com.bizmate.salesPages.management.order.orderItem.domain.QOrderItem.class, PathInits.DIRECT2);

    public final NumberPath<Long> orderNo = createNumber("orderNo", Long.class);

    public final StringPath orderNote = createString("orderNote");

    public final StringPath orderStatus = createString("orderStatus");

    public final StringPath projectId = createString("projectId");

    public final StringPath projectName = createString("projectName");

    public final NumberPath<java.math.BigDecimal> totalSubAmount = createNumber("totalSubAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalVatAmount = createNumber("totalVatAmount", java.math.BigDecimal.class);

    public final StringPath userId = createString("userId");

    public final StringPath writer = createString("writer");

    public QOrder(String variable) {
        super(Order.class, forVariable(variable));
    }

    public QOrder(Path<? extends Order> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrder(PathMetadata metadata) {
        super(Order.class, metadata);
    }

}

