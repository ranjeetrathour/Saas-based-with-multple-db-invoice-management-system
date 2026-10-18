package com.moptra.invoice.user.repository.rd;

import com.moptra.invoice.user.entities.UserIdentity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, String> {
    boolean existsByEmail(@NotBlank @Email String adminEmail);
    Optional<UserIdentity> findByEmail(String email);
}
