package com.umc.greaming.domain.user.repository;

import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.enums.UserState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

    List<User> findByNicknameContainingAndUserState(String nickname, UserState userState);
}