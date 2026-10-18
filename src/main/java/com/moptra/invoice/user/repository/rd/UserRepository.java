package com.moptra.invoice.user.repository.rd;

import com.moptra.invoice.user.entities.rd.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
