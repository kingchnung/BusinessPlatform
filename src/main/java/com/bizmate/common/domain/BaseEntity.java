package com.bizmate.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ✅ BaseEntity
 * 모든 도메인 엔티티가 상속받는 공통 부모 클래스.
 * Auditing(Auto create/update tracking) + 도메인 내부 제어용 protected setter 제공.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /** 생성 시각 */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 수정 시각 */
    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedAt;

    /** 생성자  */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    /** 최종 작성자 */
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    /* ✅ 도메인 내부(Aggregate Root 내부)에서만 접근 가능한 Setter */
    protected void setCreatedBy(String empName) { this.createdBy = empName; }
    protected void setUpdatedBy(String empName) { this.updatedBy = empName; }
    protected void setUpdatedAt(LocalDateTime time) { this.updatedAt = time; }
    protected void setCreatedAt(LocalDateTime time) { this.createdAt = time; }
}
