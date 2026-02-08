package com.umc.greaming.common;

import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.enums.UserState;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter // 컨트롤러에서 userDetails.getUser()를 쓰기 위해 필수
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // 1. 일반 로그인용 생성자 (혹시 모를 확장성 대비)
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // 2. OAuth2 로그인용 생성자 (주로 사용됨)
    public UserDetailsImpl(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // --- [권한 처리] ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // UserState를 권한으로 매핑 (예: "ACTIVE")
        // 권한이 필요 없다면 return Collections.emptyList(); 하셔도 됩니다.
        if (user.getUserState() != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(user.getUserState().name()));
        }
        return Collections.emptyList();
    }

    // --- [UserDetails 메서드 (Spring Security 내부용)] ---

    @Override
    public String getPassword() {
        // 소셜 로그인은 비밀번호가 없으므로 null 반환
        return null;
    }

    @Override
    public String getUsername() {
        // 식별자로 사용할 값. User 엔티티에 이메일이 없으므로 'userId(PK)'를 문자열로 반환
        return String.valueOf(user.getUserId());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 예: UserState가 INACTIVE나 DELETED가 아니어야 잠기지 않음
        return user.getUserState() == UserState.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부. ACTIVE 상태일 때만 로그인 가능하게 설정
        return user.getUserState() == UserState.ACTIVE;
    }

    // --- [OAuth2User 메서드 (소셜 로그인 정보)] ---

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // OAuth2User로서의 이름 (PK나 닉네임 반환)
        return String.valueOf(user.getUserId());
    }
}