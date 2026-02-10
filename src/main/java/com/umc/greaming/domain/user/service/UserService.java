package com.umc.greaming.domain.user.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.tag.entity.Tag;
import com.umc.greaming.domain.tag.repository.TagRepository;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
import com.umc.greaming.domain.user.dto.request.UpdateUserInfoRequest;
import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.UserInterestTag;
import com.umc.greaming.domain.user.entity.UserProfile;
import com.umc.greaming.domain.user.entity.UserSpecialtyTag;
import com.umc.greaming.domain.user.repository.UserInterestTagRepository;
import com.umc.greaming.domain.user.repository.UserProfileRepository;
import com.umc.greaming.domain.user.repository.UserRepository;
import com.umc.greaming.domain.user.repository.UserSpecialtyTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TagRepository tagRepository;
    private final UserSpecialtyTagRepository userSpecialtyTagRepository;
    private final UserInterestTagRepository userInterestTagRepository;

    @Transactional(readOnly = true)
    public boolean isProfileRegistered(Long userId) {
        User user = findUser(userId);
        return user.isProfileRegistered();
    }

    @Transactional
    public void registInfo(Long userId, RegistInfoRequest request) {
        User user = findUser(userId);

        if (user.isProfileRegistered()) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_REGISTERED);
        }

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .usagePurpose(request.usagePurpose())
                .weeklyGoalScore(request.weeklyGoalScore())
                .build();

        userProfileRepository.save(userProfile);

        saveSpecialtyTags(user, request.specialtyTags());
        saveInterestTags(user, request.interestTags());

        user.registerProfile(request.nickname(), request.intro());
    }

    @Transactional
    public void updateInfo(Long userId, UpdateUserInfoRequest request) {
        User user = findUser(userId);
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PROFILE_NOT_FOUND));

        user.updateInfo(request.nickname(), request.intro());
        profile.updateInfo(request.usagePurpose(), request.weeklyGoalScore());

        if (request.specialtyTags() != null) {
            userSpecialtyTagRepository.deleteAllByUser(user);
            userSpecialtyTagRepository.flush();
            saveSpecialtyTags(user, request.specialtyTags());
        }
        if (request.interestTags() != null) {
            userInterestTagRepository.deleteAllByUser(user);
            userInterestTagRepository.flush();
            saveInterestTags(user, request.interestTags());
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private void saveSpecialtyTags(User user, List<String> tagNames) {
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                userSpecialtyTagRepository.save(
                        UserSpecialtyTag.builder().user(user).tag(tag).build()
                );
            }
        }
    }

    private void saveInterestTags(User user, List<String> tagNames) {
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                userInterestTagRepository.save(
                        UserInterestTag.builder().user(user).tag(tag).build()
                );
            }
        }
    }
}
