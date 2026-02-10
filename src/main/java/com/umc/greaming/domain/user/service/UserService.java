package com.umc.greaming.domain.user.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.tag.entity.Tag;
import com.umc.greaming.domain.tag.repository.TagRepository;
import com.umc.greaming.domain.user.dto.request.RegistInfoRequest;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return user.isProfileRegistered();
    }

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
                .build();

        userProfileRepository.save(userProfile);

        saveTags(user, request.specialtyTags(), request.interestTags());

        user.registerProfile(request.nickname(), request.intro());
    }

    private void saveTags(User user, List<String> specialtyTagNames, List<String> interestTagNames) {
        if (specialtyTagNames != null) {
            for (String tagName : specialtyTagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                userSpecialtyTagRepository.save(
                        UserSpecialtyTag.builder().user(user).tag(tag).build()
                );
            }
        }

        if (interestTagNames != null) {
            for (String tagName : interestTagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                userInterestTagRepository.save(
                        UserInterestTag.builder().user(user).tag(tag).build()
                );
            }
        }
    }
}
