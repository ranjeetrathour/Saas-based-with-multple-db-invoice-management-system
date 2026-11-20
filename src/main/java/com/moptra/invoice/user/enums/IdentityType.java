package com.moptra.invoice.user.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines whether an identity belongs to the SaaS platform, a vendor tenant, or a client portal user.
 * FUTURE CHANGE POINT: Add more scopes only if external auditors or third-party reviewers are introduced.
 */
@Schema(description = "Scope of the user identity")
public enum IdentityType {
    PLATFORM,
    TENANT,
    CLIENT
}