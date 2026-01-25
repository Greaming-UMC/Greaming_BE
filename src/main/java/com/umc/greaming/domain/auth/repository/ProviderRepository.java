package com.umc.greaming.domain.auth.repository;

import com.umc.greaming.domain.auth.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByNameAndEmail(String name, String email);
}
