package com.moptra.invoice.user.dto.res;

//import com.moptra.invoice.roles.entites.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response payload after creating a vendor user and employee profile")
public record UserResponse(
        @Schema(description = "Tenant DB User ID", example = "usr_101")
        String userId,

        @Schema(description = "Full name", example = "Amit Verma")
        String name,

        @Schema(description = "Work email", example = "amit.verma@shyftlab.io")
        String email,

//        @Schema(description = "Vendor role", example = "EMPLOYEE")
//        UserRole role,

        @Schema(description = "Business Employee Code", example = "EMP-1024")
        String employeeId,

        @Schema(description = "Designation", example = "Senior Backend Engineer")
        String designation,

        @Schema(description = "Department", example = "Engineering")
        String department,

        @Schema(description = "Assigned vendor branch ID", example = "br_01")
        String branchId,

        @Schema(description = "Joining date", example = "2026-04-01")
        LocalDate joiningDate
) {}