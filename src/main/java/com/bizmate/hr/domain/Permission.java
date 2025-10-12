package com.bizmate.hr.domain;

import com.bizmate.hr.domain.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * [권한 Entity - Permission]
 * 데이터베이스의 PERMISSIONS 테이블과 매핑됩니다.
 * Spring Security에서 세부적인 기능 접근 제어에 사용될 수 있습니다.
 */
@Entity
@Table(name = "PERMISSIONS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    // 1. 권한ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permi_id")
    private Long permiId;

    // 2. 권한명 (유일키, 필수)
    @Column(name = "permi_name", unique = true, nullable = false, length = 100)
    private String permiName;

    // 3. 설명
    @Column(name = "description", length = 255)
    private String description;

    // N:M 관계 매핑: 역할 - 권한 (ROLE_PERMISSIONS 테이블)
    // Role 엔티티에서 JoinTable을 통해 매핑되었으므로 mappedBy를 사용합니다.
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}