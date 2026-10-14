package com.moptra.invoice.user.repository;

import com.moptra.invoice.user.entities.mongo.TenantConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantConfigRepository extends MongoRepository<TenantConfigEntity, String> {

    Optional<TenantConfigEntity> findByTenantCode(String tenantCode);
}