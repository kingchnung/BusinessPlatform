package com.bizmate.hr.service;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.member.LoginRequestDTO;
import com.bizmate.hr.dto.member.LoginResponseDTO;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.hr.security.jwt.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    // 필요한 의존성 주입
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;

    /**
     * [로그인 처리]
     * 1. DB에서 사용자 ID로 UserEntity 조회
     * 2. 입력된 비밀번호와 DB의 암호화된 비밀번호 비교
     * 3. 인증 성공 시 Access/Refresh Token 발급 및 응답 DTO 구성
     */
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {
        log.info("로그인 시도: {}", requestDTO.getUsername());

        // 1. 사용자 조회 (UserEntity는 Employee와 FetchType.EAGER로 설정되어 있어야 NullPointerException 방지)
        UserEntity userEntity = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new BadCredentialsException("사용자 ID를 찾을 수 없습니다."));

        // ★★★ 2. 비밀번호 검증 (500 에러의 주요 원인) ★★★
        // 입력된 비밀번호와 DB에 저장된 암호화된 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(requestDTO.getPassword(), userEntity.getPwHash())) {
            log.warn("비밀번호 불일치: {}", requestDTO.getUsername());
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        UserDTO userDTO = UserDTO.fromEntity(userEntity);

        // 3. 권한 및 역할 목록 추출
        List<String> roles = userEntity.getRoles().stream()
                .map(r -> r.getRoleName())
                .collect(Collectors.toList());

        List<String> perms = userEntity.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getPermName())
                .distinct()
                .collect(Collectors.toList());

        // 4. JWT Access/Refresh Token 생성
        String accessToken = jwtProvider.createAccessToken(userDTO, roles, perms);
        String refreshToken = jwtProvider.createRefreshToken(userDTO);

        // 5. 응답 DTO 구성 및 반환
        return LoginResponseDTO.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUsername())
                .empId(userEntity.getEmployee().getEmpId())
                .empName(userEntity.getEmployee().getEmpName())
                .roles(roles)
                .perms(perms)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}