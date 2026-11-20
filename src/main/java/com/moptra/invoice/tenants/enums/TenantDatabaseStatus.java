package com.moptra.invoice.tenants.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Status of tenant database provisioning or connectivity check.
 * FUTURE CHANGE POINT: Add MAINTENANCE or ROTATING_CREDENTIALS if DB maintenance states are tracked.
 */
@Schema(description = "Status of tenant database provisioning/connectivity")
public enum TenantDatabaseStatus {
    PROVISIONING,
    ACTIVE,
    FAILED
}