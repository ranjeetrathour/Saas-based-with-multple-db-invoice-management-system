package com.moptra.invoice.roles.controller;

import com.moptra.invoice.roles.dto.req.UserRoleReq;
import com.moptra.invoice.roles.dto.res.UserRoleRes;
import com.moptra.invoice.roles.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

/**
 * REST Controller for managing custom roles inside the active Tenant DB.
 *
 * FUTURE CHANGE POINT:
 * - Add @PreAuthorize role/permission guard and endpoints to assign/revoke
 *   permissions on a role once Permission management is implemented.
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Tenant Roles", description = "Creates and manages custom roles inside the active tenant database")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    @Operation(summary = "Create tenant role", description = "Creates a custom role in the active tenant database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Role created successfully",
                    content = @Content(schema = @Schema(implementation = UserRoleRes.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid tenant context", content = @Content),
            @ApiResponse(responseCode = "409", description = "Role name already exists", content = @Content)
    })
    public ResponseEntity<UserRoleRes> createRole(@Valid @RequestBody UserRoleReq request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userRoleService.createRole(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Fetches a single role from the active tenant database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    public ResponseEntity<UserRoleRes> getRoleById(
            @Parameter(description = "Role ID", required = true) @PathVariable String id) {
        return ResponseEntity.ok(userRoleService.getRoleById(id));
    }

    @GetMapping
    @Operation(summary = "List tenant roles", description = "Returns all roles defined in the active tenant database")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    public ResponseEntity<List<UserRoleRes>> getAllRoles() {
        return ResponseEntity.ok(userRoleService.getAllRoles());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant role", description = "Updates role name and description in the active tenant database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Role name already exists", content = @Content)
    })
    public ResponseEntity<UserRoleRes> updateRole(
            @Parameter(description = "Role ID", required = true) @PathVariable String id,
            @Valid @RequestBody UserRoleReq request) {
        return ResponseEntity.ok(userRoleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant role", description = "Deletes an unassigned role from the active tenant database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Role is currently assigned to users", content = @Content)
    })
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "Role ID", required = true) @PathVariable String id) {
        userRoleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}