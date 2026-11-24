package com.moptra.invoice.roles.repository;

import com.moptra.invoice.roles.entites.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    boolean existsByNameIgnoreCase(String name);

    Optional<UserRole> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    @Query(value = "SELECT * FROM user_roles WHERE id = 'role_company_admin' AND name = 'COMPANY_ADMIN'", nativeQuery = true)
    Optional<UserRole> fetchCompanyAdminRoleData();

    @Query(value = "SELECT * FROM user_roles WHERE id = 'role_client_admin' AND name = 'CLIENT_ADMIN'", nativeQuery = true)
    Optional<UserRole> fetchClientAdminRoleData();
}