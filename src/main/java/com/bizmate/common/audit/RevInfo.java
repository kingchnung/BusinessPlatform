package com.bizmate.common.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "REVINFO")
@RevisionEntity(SecurityRevisionListener.class)
public class RevInfo implements Serializable {

    @Id
    @SequenceGenerator(name = "revinfo_seq", sequenceName = "REVINFO_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq")
    @RevisionNumber
    private Integer id;            // = revision number

    @RevisionTimestamp
    private Long timestamp;        // millis since epoch

    private String modifierId;    // 로그인 ID
    private String modifierName;  // 실명
    private String modifierFull;  // "홍길동 (hong.gildong)"
}