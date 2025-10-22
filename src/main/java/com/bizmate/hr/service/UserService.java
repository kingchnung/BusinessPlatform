package com.bizmate.hr.service;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.dto.user.UserUpdateRequestDTO;

import java.util.List;

public interface UserService {

    /**
     * 신규 직원 생성 시 사용자 계정을 자동 생성합니다.
     * @param employee 새로 생성된 직원 엔티티
     * @param initialPassword 초기 비밀번호
     * @return 생성된 사용자 계정 DTO
     */
    UserDTO createUserAccount(Employee employee, String initialPassword);
    UserDTO createUserAccount(Employee employee);
    /**
     * 전체 사용자 계정 목록을 조회합니다. (관리자 기능: GET /api/users)
     * @return UserDTO 리스트
     */
    List<UserDTO> findAllUsers();

    /**
     * 특정 사용자 계정 정보를 조회합니다. (GET /api/users/{userId})
     * @param userId 조회할 사용자 ID
     * @return 사용자 계정 DTO
     */
    UserDTO getUser(Long userId);

    /**
     * 특정 사용자 계정 정보를 수정합니다. (관리자 기능: PUT /api/users/{userId})
     * (계정 잠금 상태, 역할 수정)
     * @param userId 수정할 사용자 ID
     * @param updateDTO 수정 정보
     * @return 수정된 사용자 계정 DTO
     */
    UserDTO updateUser(Long userId, UserUpdateRequestDTO updateDTO);

    /**
     * 특정 사용자 계정을 삭제합니다. (관리자 기능: DELETE /api/users/{userId})
     * @param userId 삭제할 사용자 ID
     */
    void deleteUser(Long userId);

}
