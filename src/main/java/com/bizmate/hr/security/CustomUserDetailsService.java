package com.bizmate.hr.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.repository.UserRepository; // 가정된 Repository 경로
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * [CustomUserDetailsService]
 * - Spring Security의 인증 처리를 위해 사용자 정보를 DB에서 로드하는 핵심 서비스
 * - 로그인 ID (username)을 사용하여 User 엔티티를 조회하고 UserDetails(UserDTO)로 변환합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    // UserRepository를 주입받아 DB 접근
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("▶▶▶ loadUserByUsername: 사용자 정보를 로드합니다. [입력된 ID: {}] ", username);

        // 1. username으로 DB에서 User 정보 및 연관 엔티티(Role, Permission, Employee)를 조회
        UserEntity userEntity = userRepository.getWithRolesAndPermissions(username);

        // 2. 조회 결과가 없을 경우 예외 처리
        if (userEntity == null) {
            log.warn("사용자 정보를 찾을 수 없습니다. [ID: {}]", username);
            throw new UsernameNotFoundException("해당 로그인 ID의 사용자 정보를 찾을 수 없습니다.");
        }

        // 3. User 엔티티를 UserDTO로 변환하여 Spring Security에 전달
        // UserDTO의 fromEntity 메서드를 사용하여 권한 목록을 포함하여 객체 생성
        UserDTO userDTO = UserDTO.fromEntity(userEntity);

        log.info("인증을 위해 DTO로 변환된 사용자 정보: {}", userDTO);

        return userDTO;
    }
}