package com.bizmate.hr.service;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.Role;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.dto.user.UserUpdateRequestDTO;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.RoleRepository;
import com.bizmate.hr.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * 신규 직원 생성 시 사용자 계정을 자동 생성하고 기본 역할을 부여합니다.
     */
    @Override
    @Transactional
    public UserDTO createUserAccount(Employee employee, String initialPassword) {
        // 1. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(initialPassword);

        // 2. UserEntity 생성 및 기본 설정
        UserEntity user = new UserEntity();
        user.setUsername(employee.getEmpNo());
        user.setPwHash(encodedPassword);
        user.setEmployee(employee);
        user.setIsLocked("N"); // 초기에는 잠금 해제 상태

        // 3. 역할 초기 설정: 'EMPLOYEE' 역할을 부여합니다.
        Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new EntityNotFoundException("기본 역할 'EMPLOYEE'를 찾을 수 없습니다. Role 테이블 확인 필요."));

        // UserEntity의 @ManyToMany 관계 필드를 초기화하고 역할 추가
        Set<Role> roles = new HashSet<>(Collections.singletonList(employeeRole));
        user.setRoles(roles);

        // 4. 저장
        UserEntity savedUser = userRepository.save(user);
        log.info("직원 {} 에 대한 사용자 계정 {} 자동 등록 완료. (UserID: {})", employee.getEmpName(), savedUser.getUsername(), savedUser.getUserId());

        // 5. DTO 변환 및 반환 (UserDTO는 Employee와 Role/Permission 정보가 필요)
        // savedUser 엔티티에는 영속성 컨텍스트에서 Role 및 Employee 정보가 로드되어야 UserDTO 변환이 정상 작동합니다.
        return UserDTO.fromEntity(savedUser);
    }

    /**
     * 전체 사용자 계정 목록을 조회합니다. (관리자 기능)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        // [TODO: JPA 최적화 필수] N+1 문제를 방지하기 위해 UserEntity, Employee, Role, Permission을 모두 Fetch Join 해야 합니다.
        // 예: userRepository.findAllWithDetails(); 와 같은 메서드 사용 권장
        List<UserEntity> users = userRepository.findAll();

        // DTO 변환 시, fromEntity에서 Employee와 Role 정보를 사용하므로 지연 로딩 문제가 발생하지 않도록 Repository 단에서 처리해야 함
        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자 계정 정보를 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUser(Long userId) {
        // [TODO: JPA 최적화 필수] Role, Employee 정보를 Fetch Join하여 가져와야 합니다.
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("사용자 ID " + userId + "를 찾을 수 없습니다."));

        return UserDTO.fromEntity(user);
    }

    /**
     * 특정 사용자 계정 정보를 수정합니다. (계정 잠금 상태, 역할, 비밀번호 수정)
     */
    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserUpdateRequestDTO updateDTO) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("수정하려는 사용자 ID " + userId + "를 찾을 수 없습니다."));

        // 1. 계정 잠금 상태 업데이트
        // isAccountNonLocked: true (잠금 해제) -> isLocked: "N"
        // isAccountNonLocked: false (잠금)   -> isLocked: "Y"
        user.setIsLocked(updateDTO.isAccountNonLocked() ? "N" : "Y");
        log.info("사용자 ID {} 계정 잠금 상태 변경: {}", userId, updateDTO.isAccountNonLocked() ? "해제(N)" : "잠금(Y)");

        // 2. 비밀번호 재설정
        if (updateDTO.getNewPassword() != null && !updateDTO.getNewPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updateDTO.getNewPassword());
            user.setPwHash(encodedPassword);
            log.info("사용자 ID {} 의 비밀번호가 재설정되었습니다.", userId);
        }

        // 3. 역할(Role) 업데이트
        List<Long> roleIds = updateDTO.getRoleIds();
        if (roleIds != null) {

            if (roleIds.isEmpty()) {
                user.getRoles().clear();
                log.warn("사용자 ID {} 의 모든 역할이 제거되었습니다.", userId);
            } else {
                List<Role> newRoles = roleRepository.findAllById(roleIds);

                if (newRoles.size() != roleIds.size()) {
                    throw new EntityNotFoundException("요청된 역할(Role) ID 중 유효하지 않은 ID가 포함되어 있습니다.");
                }

                // 기존 역할을 지우고 새로운 역할 목록으로 대체
                user.getRoles().clear();
                user.getRoles().addAll(newRoles);
                log.info("사용자 ID {} 역할이 {} 개로 업데이트되었습니다.", userId, newRoles.size());
            }
        }

        // DTO 변환 시, 변경된 UserEntity를 기반으로 새로운 UserDTO(권한 포함)를 생성하여 반환합니다.
        return UserDTO.fromEntity(user);
    }

    /**
     * 특정 사용자 계정을 삭제합니다.
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("삭제하려는 사용자 ID {} 가 존재하지 않아 삭제를 건너뜁니다.", userId);
            throw new EntityNotFoundException("삭제하려는 사용자 ID " + userId + "를 찾을 수 없습니다.");
        }
        userRepository.deleteById(userId);
        log.info("사용자 ID {} 의 계정이 성공적으로 삭제되었습니다.", userId);
    }
}
