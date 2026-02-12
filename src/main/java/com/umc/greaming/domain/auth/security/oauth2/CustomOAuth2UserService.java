package com.umc.greaming.domain.auth.security.oauth2;

import com.umc.greaming.domain.auth.entity.Provider;
import com.umc.greaming.domain.auth.repository.ProviderRepository;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.enums.UserState;
import com.umc.greaming.domain.user.enums.Visibility;
import com.umc.greaming.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        Provider provider = providerRepository.findByNameAndEmail(registrationId, userInfo.getEmail())
                .orElse(null);

        boolean isNewUser = false;
        User user;

        if (provider == null) {
            // 신규 사용자 생성
            isNewUser = true;
            user = createUser(userInfo);
            createProvider(registrationId, userInfo, user);
            log.info("신규 사용자 생성: userId={}, provider={}", user.getUserId(), registrationId);
        } else {
            user = userRepository.findById(provider.getUserId())
                    .orElseThrow(() -> new OAuth2AuthenticationException("사용자를 찾을 수 없습니다."));

            if (user.isDeleted()) {
                // 탈퇴한 유저 → 기존 Provider 해제 후 새 계정 생성
                providerRepository.delete(provider);
                providerRepository.flush();
                isNewUser = true;
                user = createUser(userInfo);
                createProvider(registrationId, userInfo, user);
                log.info("탈퇴 유저 재가입: userId={}, provider={}", user.getUserId(), registrationId);
            } else {
                log.info("기존 사용자 로그인: userId={}, provider={}", user.getUserId(), registrationId);
            }
        }

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                userNameAttributeName,
                user.getUserId(),
                isNewUser
        );
    }

    private User createUser(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .name(userInfo.getName())
                .nickname(generateUniqueNickname())
                .profileImageKey(userInfo.getProfileImageUrl())
                .userState(UserState.ACTIVE)
                .visibility(Visibility.PUBLIC)
                .build();

        return userRepository.save(user);
    }

    private void createProvider(String registrationId, OAuth2UserInfo userInfo, User user) {
        Provider provider = Provider.builder()
                .name(registrationId)
                .user(user)
                .nickname(userInfo.getName())
                .email(userInfo.getEmail())
                .build();

        providerRepository.save(provider);
    }

    private String generateUniqueNickname() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }
}