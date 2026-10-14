package com.moptra.invoice.user.controller;

import com.moptra.invoice.user.dto.req.CreateEmployeeRequest;
import com.moptra.invoice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateEmployeeRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("User created successfully in Master DB and Tenant DB.");
    }
}