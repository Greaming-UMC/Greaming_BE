package com.umc.greaming.domain.auth.security.oauth2;

import java.util.Map;
import java.util.Optional;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getEmail() {
        return getKakaoAccount()
                .map(account -> getStringValue(account, "email"))
                .orElse(null);
    }

    @Override
    public String getName() {
        return getProfile()
                .map(profile -> getStringValue(profile, "nickname"))
                .orElse(null);
    }

    @Override
    public String getProfileImageUrl() {
        return getProfile()
                .map(profile -> getStringValue(profile, "profile_image_url"))
                .orElse(null);
    }

    private Optional<Map<String, Object>> getKakaoAccount() {
        return getMapValue(attributes, "kakao_account");
    }

    private Optional<Map<String, Object>> getProfile() {
        return getKakaoAccount()
                .flatMap(account -> getMapValue(account, "profile"));
    }

    @SuppressWarnings("unchecked")
    private Optional<Map<String, Object>> getMapValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return Optional.of((Map<String, Object>) value);
        }
        return Optional.empty();
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : null;
    }
}