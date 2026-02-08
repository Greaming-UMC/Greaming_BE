package com.umc.greaming.common;

import com.umc.greaming.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter // 컨트롤러에서 userDetails.getUser()를 사용하기 위해 필수
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // 1. 일반 로그인용 생성자
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // 2. 소셜 로그인용 생성자 (OAuth2)
    public UserDetailsImpl(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 소셜 로그인의 경우 null일 수 있음
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 고유 식별자로 이메일 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // --- [OAuth2User 메서드 (소셜 로그인)] ---
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail(); // 소셜 로그인 식별자로도 이메일 사용
    }
}