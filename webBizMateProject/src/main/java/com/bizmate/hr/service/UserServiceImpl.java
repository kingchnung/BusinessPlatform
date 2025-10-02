package com.bizmate.hr.service;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.Role;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.RoleRepository;
import com.bizmate.hr.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDTO createUserAccount(Employee employee, String initialPassword) {
        // 1. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(initialPassword);

        // 2. UserEntity 생성: username은 empNo를 사용하고, employee FK 설정
        UserEntity user = new UserEntity();
        user.setUsername(employee.getEmpNo()); // ★ empNo를 ID로 사용
        user.setPwHash(encodedPassword);
        user.setEmployee(employee);
        user.setIsLocked("N");

        // 3. 권한 및 역할 초기 설정 (자동 생성된 직원은 'USER'와 기본 권한만 부여)
        Role employeeRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new UsernameNotFoundException("기본 역할 'EMPLOYEE'를 찾을 수 없습니다."));

        Set<Role> roles = new HashSet<>(Collections.singletonList(employeeRole));

        // 4. 저장 및 DTO 반환
        UserEntity savedUser = userRepository.save(user);
        log.info("직원 {} 에 대한 사용자 계정 {} 자동 등록 완료.", employee.getEmpName(), savedUser.getUsername());

        return UserDTO.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("사용자 ID " + userId + "를 찾을 수 없습니다."));
        return UserDTO.fromEntity(user);
    }
}
