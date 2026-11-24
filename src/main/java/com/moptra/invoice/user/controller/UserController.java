package com.moptra.invoice.user.controller;

import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.dto.res.UserResponse;
import com.moptra.invoice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for creating internal Vendor/Tenant users and employee profiles
 * (e.g., Shyftlab HR, Finance, Managers, Developers).
 *
 * FUTURE CHANGE POINT:
 * - Restrict access via @PreAuthorize once RBAC roles/permissions are enabled.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Vendor User & Employee Management", description = "Creates vendor internal users and employee records")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(
            summary = "Create vendor user and employee",
            description = "Creates a TENANT login identity in Master DB and User + Employee records in Tenant DB"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Vendor user and employee created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid role", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or employeeId already exists", content = @Content)
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }
}