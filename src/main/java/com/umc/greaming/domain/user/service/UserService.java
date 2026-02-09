package com.umc.greaming.domain.user.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.UserProfile;
import com.umc.greaming.domain.user.repository.UserProfileRepository;
import com.umc.greaming.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void registInfo(Long userId, RegistInfoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (user.isProfileRegistered()) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_REGISTERED);
        }

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .usagePurpose(request.usagePurpose())
                .weeklyGoalScore(request.weeklyGoalScore())
                .specialtyStyle(request.specialties().style())
                .interestStyle(request.interests().style())
                .specialtyFields(request.specialties().fields())
                .interestFields(request.interests().fields())
                .build();

        userProfileRepository.save(userProfile);

        user.registerProfile(request.nickname(), request.intro());
    }
}
