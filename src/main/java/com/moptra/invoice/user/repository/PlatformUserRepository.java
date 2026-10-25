package com.moptra.invoice.user.repository;

import com.moptra.invoice.user.entities.rd.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PlatformUserRepository extends JpaRepository<PlatformUser, String> {
   Optional<PlatformUser> findByEmail(String email);
}
