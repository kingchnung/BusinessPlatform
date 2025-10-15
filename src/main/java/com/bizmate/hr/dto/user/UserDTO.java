package com.bizmate.hr.dto.user;

import com.bizmate.hr.domain.Role;
import com.bizmate.hr.domain.Permission;
import com.bizmate.hr.domain.UserEntity; // 엔티티 이름 변경 적용

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "pwHash")
public class UserDTO extends User {
    private static final long serialVersionUID = 1L;

    // DTO의 final 필드들
    private final Long userId;
    private final Long empId;
    private final String departmentCode;
    private final String username;
    private final String pwHash;
    private final String empName;
    private final boolean isAccountNonLocked;
    private final List<String> roleNames;
    private final List<String> permissionNames;

    public UserDTO(Long userId, Long empId, String empName, String departmentCode, String username) {
        super(empName, "", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))); // ✅ 최소한의 super 호출
        this.userId = userId;
        this.empId = empId;
        this.departmentCode = departmentCode;
        this.empName = empName;
        this.username = username;
        this.pwHash = "";
        this.isAccountNonLocked = true;
        this.roleNames = Collections.emptyList();
        this.permissionNames = Collections.emptyList();
    }


    /**
     * Spring Security의 UserDetails를 상속받는 생성자
     * 주의: authorities 컬렉션을 외부(fromEntity)에서 미리 생성하여 전달받습니다.
     */
    public UserDTO(
            Long userId,
            Long empId,
            String departmentCode,
            String username,
            String pwHash,
            String empName,
            boolean isAccountNonLocked,
            List<String> roleNames,
            List<String> permissionNames,
            // ★★★ 이미 생성된 authorities를 인수로 받음 ★★★
            Collection<? extends GrantedAuthority> authorities) {

        // 1. super() 호출 (첫 줄)
        // username, password(pwHash), enabled(true), accountNonExpired(true),
        // credentialsNonExpired(true), accountNonLocked, authorities
        super(username, pwHash, true, true, true, isAccountNonLocked, authorities);

        // 2. DTO 필드 초기화 (super() 호출 후)
        this.userId = userId;
        this.empId = empId;
        this.departmentCode = departmentCode;
        this.username = username;
        this.pwHash = pwHash;
        this.empName = empName;
        this.isAccountNonLocked = isAccountNonLocked;
        this.roleNames = roleNames;
        this.permissionNames = permissionNames;
    }

    // --- Private Static 헬퍼 메서드 영역 ---

    /**
     * Spring Security의 Authorities 목록을 생성합니다. (DTO에서 독립적인 기능)
     */
    private static Collection<? extends GrantedAuthority> createAuthorities(
            List<String> roleNames, List<String> permissionNames) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 역할(Role) 등록: "ROLE_" 접두어 사용
        roleNames.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .forEach(authorities::add);

        // 권한(Permission) 등록: 세부 권한 체크에 사용
        permissionNames.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        return authorities;
    }

    /**
     * JWT 클레임 생성을 위한 Map 반환 메서드
     */
    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("userId", userId);
        dataMap.put("empId", empId);
        dataMap.put("departmentCode", departmentCode);
        dataMap.put("username", username);
        dataMap.put("empName", empName);

        dataMap.put("roles", roleNames);
        dataMap.put("perms", permissionNames);

        return dataMap;
    }

    /**
     * User Entity -> UserDTO 변환을 위한 헬퍼 메서드
     * (이 메서드에서 authorities를 생성하여 생성자에 전달)
     */
    public static UserDTO fromEntity(UserEntity user) {

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        List<String> permissionNames = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermName)
                .distinct()
                .collect(Collectors.toList());

        boolean isLocked = false;
        boolean isAccountNonLocked = !isLocked;

        // ★★★ Authorities를 미리 생성 ★★★
        Collection<? extends GrantedAuthority> authorities =
                createAuthorities(roleNames, permissionNames);

        return new UserDTO(
                user.getUserId(),
                user.getEmployee().getEmpId(),
                user.getDeptCode(),
                user.getUsername(),
                user.getPwHash(),
                user.getEmployee().getEmpName(),
                isAccountNonLocked,
                roleNames,
                permissionNames,
                authorities // ★★★ 생성된 authorities 전달 ★★★
        );
    }
}