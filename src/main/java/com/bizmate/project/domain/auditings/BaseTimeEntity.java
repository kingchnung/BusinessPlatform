package com.bizmate.project.domain.auditings;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
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
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {


    // 등록시간 매핑
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;


    // 수정시간 매핑
    @LastModifiedDate
    @Column(name = "mod_date")
    private LocalDateTime modDate;

    @CreatedBy
    @Column(name = "reg_id", updatable = false)
    private String regId;

    @LastModifiedBy
    @Column(name = "mod_id")
    private String modId;

    @PrePersist
    public void onPrePersist() {
        this.modDate = null;
        this.modId = null;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modDate = LocalDateTime.now();
    }

}
