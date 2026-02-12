package com.umc.greaming.domain.auth.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User extends DefaultOAuth2User {

    private final Long userId;
    private final boolean isNewUser;

    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            Long userId,
            boolean isNewUser
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.userId = userId;
        this.isNewUser = isNewUser;
    }

    /**
     * Controller / Service 계층에서 의미 있는 접근용 메서드
     */
    public Long getUserId() {
        return userId;
    }

    public boolean isNewUser() {
        return isNewUser;
    }
}
