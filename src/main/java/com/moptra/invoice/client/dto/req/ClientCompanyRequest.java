package com.moptra.invoice.client.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Request record for onboarding a Client Company (e.g., Petco) under the Main Tenant / Vendor
 * (e.g., Shyftlab), with optional embedded branches[cite: 1, 2].
 *
 * FUTURE CHANGE POINT: Add optional initial client portal user details (e.g., clientAdminName,
 * clientAdminEmail) here if a restricted CLIENT_ADMIN login should be auto-provisioned
 * during client onboarding.
 */
@Schema(description = "Payload to onboard a client company and optional branches")
public record ClientCompanyRequest(
        @NotBlank(message = "Company name is required")
        @Schema(description = "Client company name", example = "Petco")
        String companyName,

        @NotBlank(message = "Company code is required")
        @Schema(description = "Client company code (unique within vendor)", example = "PETCO")
        String companyCode,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "Primary contact or billing email of the client company", example = "ap@petco.com")
        String email,

        @Schema(description = "Client contact phone number", example = "+1-800-555-0199")
        String phone,

        @Schema(description = "Primary GST or tax registration number", example = "27AABCP1234C1Z2")
        String gst,

        @Schema(description = "Primary billing address for invoices", example = "10850 Via Frontera, San Diego")
        String billingAddress,

        @Schema(description = "City name", example = "San Diego")
        String city,

        @Schema(description = "State name", example = "California")
        String state,

        @Schema(description = "Country name", example = "USA")
        String country,

        @Valid
        @Schema(description = "Optional list of branches to create alongside the client company")
        List<ClientBranchRequest> branches
) {}