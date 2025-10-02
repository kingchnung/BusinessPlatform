package com.bizmate.hr.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [사용자 계정 수정 요청 DTO]
 * - 관리자 페이지에서 계정 활성화/잠금 상태 변경 및 비밀번호 재설정 시 사용
 */
@Getter
@NoArgsConstructor
public class UserUpdateRequestDTO {

    // 1. 비밀번호 재설정 시 사용
    private String newPassword;     // 새 비밀번호 (서비스에서 해시 처리)

    // 2. 계정 활성화/잠금 상태 변경 시 사용 (emp_admin_004)
    private String isActive;        // 계정 활성 여부 ('Y'/'N')
    private String isLocked;        // 계정 잠금 여부 ('Y'/'N')

    // 3. 역할(Role) 변경 시 사용 (필요하다면)
    private Long roleId;
}