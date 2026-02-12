package com.umc.greaming.domain.user.repository;

import com.umc.greaming.domain.user.entity.User;
import com.umc.greaming.domain.user.entity.UserSpecialtyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSpecialtyTagRepository extends JpaRepository<UserSpecialtyTag, Long> {

    @Query("select t.name from UserSpecialtyTag ust join ust.tag t where ust.user.userId = :userId")
    List<String> findTagNamesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserSpecialtyTag ust WHERE ust.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
