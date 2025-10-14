package com.bizmate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 인증 전용 UserDetails 구현체
 * - API 응답 DTO와 철저히 분리
 * - pwHash 등 민감 정보는 @JsonIgnore로 절대 외부 노출 금지
 */
@Getter

public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final String username;

    @JsonIgnore
    private final String pwHash;

    private final boolean active;
    private final boolean locked;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userId,
                         String username,
                         String pwHash,
                         boolean active,
                         boolean locked,
                         Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.pwHash = pwHash;
        this.active = active;
        this.locked = locked;
        this.authorities = authorities;
    }

    public Long getUserId() { return userId; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return pwHash; }
    @Override public String getUsername() { return username; }

    // 활성화/잠금에 맞춰 UserDetails 표준값 매핑
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !locked; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}
