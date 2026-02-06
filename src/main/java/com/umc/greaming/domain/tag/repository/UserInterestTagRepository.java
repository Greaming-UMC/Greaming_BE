package com.umc.greaming.domain.tag.repository;

import com.umc.greaming.domain.tag.entity.UserInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInterestTagRepository extends JpaRepository<UserInterestTag, Long> {

    @Query("select t.name from UserInterestTag uit join uit.tag t where uit.user.id = :userId")
    List<String> findTagNamesByUserId(@Param("userId") Long userId);
}
