package com.umc.greaming.domain.tag.repository;

import com.umc.greaming.domain.tag.entity.UserSpecialtyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSpecialtyTagRepository extends JpaRepository<UserSpecialtyTag, Long> {

    @Query("select t.name from UserSpecialtyTag ust join ust.tag t where ust.user.id = :userId")
    List<String> findTagNamesByUserId(@Param("userId") Long userId);
}
