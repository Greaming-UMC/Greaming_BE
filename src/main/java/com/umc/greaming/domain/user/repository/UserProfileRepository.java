package com.umc.greaming.domain.user.repository;

import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);
}
