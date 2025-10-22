package com.bizmate.salesPages.client.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClient is a Querydsl query type for Client
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClient extends EntityPathBase<Client> {

    private static final long serialVersionUID = -1424102715L;

    public static final QClient client = new QClient("client");

    public final StringPath businessLicenseFile = createString("businessLicenseFile");

    public final StringPath clientAddress = createString("clientAddress");

    public final StringPath clientBusinessType = createString("clientBusinessType");

    public final StringPath clientCeo = createString("clientCeo");

    public final StringPath clientCompany = createString("clientCompany");

    public final StringPath clientContact = createString("clientContact");

    public final StringPath clientEmail = createString("clientEmail");

    public final StringPath clientId = createString("clientId");

    public final NumberPath<Long> clientNo = createNumber("clientNo", Long.class);

    public final StringPath clientNote = createString("clientNote");

    public final DatePath<java.time.LocalDate> registrationDate = createDate("registrationDate", java.time.LocalDate.class);

    public final StringPath userId = createString("userId");

    public final BooleanPath validationStatus = createBoolean("validationStatus");

    public final StringPath writer = createString("writer");

    public QClient(String variable) {
        super(Client.class, forVariable(variable));
    }

    public QClient(Path<? extends Client> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClient(PathMetadata metadata) {
        super(Client.class, metadata);
    }

}

