package com.bizmate.hr.domain.code;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGrade is a Querydsl query type for Grade
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGrade extends EntityPathBase<Grade> {

    private static final long serialVersionUID = 1404583647L;

    public static final QGrade grade = new QGrade("grade");

    public final NumberPath<Long> gradeCode = createNumber("gradeCode", Long.class);

    public final StringPath gradeName = createString("gradeName");

    public final NumberPath<Integer> gradeOrder = createNumber("gradeOrder", Integer.class);

    public final StringPath isUsed = createString("isUsed");

    public QGrade(String variable) {
        super(Grade.class, forVariable(variable));
    }

    public QGrade(Path<? extends Grade> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGrade(PathMetadata metadata) {
        super(Grade.class, metadata);
    }

}

