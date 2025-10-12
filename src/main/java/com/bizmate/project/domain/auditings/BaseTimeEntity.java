package com.bizmate.project.domain.auditings;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
// 이 클래스를 상속받는 서브 클래스들의 테이블에 이 클래스 필드들이 포함된다.
@EntityListeners(AuditingEntityListener.class)
// 생성 시간 자동 기록 , 수정 시간 자동 기록
// 생성자/ 수정자 자동 기록 어노테이션
public abstract class BaseTimeEntity {

    //updatable = false
    // 특정 필드를 데이터베이스에서 업데이트 할 수 없도록 설정하는 역할

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "mod_date")
    private LocalDateTime modDate;

    @CreatedBy
    // 엔티티가 처음 생성 될때 RIG_ID 칼럼에 생성자의 식별자를 자동으로 기록
    @Column(name = "reg_id", updatable = false)
    private String regId;

    @LastModifiedBy
    // 엔티티가 수정될때 MOD_ID 컬럼에 마지막 수정자의 식별자를 자동으로 기록
    @Column(name = "mod_id")
    private String modId;
}
