package com.moptra.invoice.user.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines whether an identity belongs to the SaaS platform ( for saas will add some more
 * enum which will act as role for the saas related users so role table will not needed for saas db
 * (can add this table later once sir will confirm )), a vendor tenant, or a client portal user.
 */
@Schema(description = "Scope of the user identity")
public enum IdentityType {
    PLATFORM,
    TENANT,
    CLIENT
}