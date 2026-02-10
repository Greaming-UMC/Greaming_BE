package com.umc.greaming.domain.user.repository;

import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.UserInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInterestTagRepository extends JpaRepository<UserInterestTag, Long> {

    @Query("select t.name from UserInterestTag uit join uit.tag t where uit.user.userId = :userId")
    List<String> findTagNamesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserInterestTag uit WHERE uit.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
