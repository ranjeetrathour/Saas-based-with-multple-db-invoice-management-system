package com.moptra.invoice.tenants.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Account status for vendor and client portal user identities.
 * FUTURE CHANGE POINT: Add LOCKED or INVITED if invite/password-reset or brute-force lock states are added.
 */
@Schema(description = "Status of a user account")
public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    PENDING
}