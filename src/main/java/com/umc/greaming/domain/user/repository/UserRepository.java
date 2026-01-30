package com.umc.greaming.domain.user.repository;

import com.umc.greaming.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
