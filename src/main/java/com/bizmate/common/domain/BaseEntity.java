package com.bizmate.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 도메인 엔티티가 상속받는 기본 엔티티 클래스.
 * 데이터 생성 및 수정 시각과 작업자를 자동으로 기록(Auditing)합니다.
 * 이를 통해 모든 데이터에 대한 메타데이터 관리 및 추적이 용이해집니다.
 */

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter
public abstract class BaseEntity {

    // 데이터가 생성된 시각
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 데이터를 최종 수정한 시각
    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedAt;

    // 데이터를 생성한 사용자 ID (실제로는 User Entity와 연결)
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    // 데이터를 최종 수정한 사용자 ID
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    // --- Getters (Setters는 일반적으로 Auditing 필드에 사용되지 않으므로 생략) ---

}
