package com.moptra.invoice.tenants.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Lifecycle states for a Main Tenant (Vendor) company.
 * FUTURE CHANGE POINT: Add states like TRIAL or ARCHIVED if trial onboarding or soft-offboarding is needed.
 */
@Schema(description = "Lifecycle status of a vendor/tenant company")
public enum CompanyStatus {
    PROVISIONING,
    ACTIVE,
    PROVISIONING_FAILED,
    SUSPENDED,
    INACTIVE
}